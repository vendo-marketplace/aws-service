package com.vendo.aws_service.adapter.presign.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.adapter.presign.in.dto.FileRequest;
import com.vendo.aws_service.adapter.presign.in.dto.PresignRequest;
import com.vendo.aws_service.adapter.presign.in.dto.PresignResponse;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.presign.dto.PresignBody;
import com.vendo.aws_service.domain.presign.type.ContextType;
import com.vendo.aws_service.port.presign.PresignQueryPort;
import com.vendo.aws_service.test_utils.security.SecurityContextTestService;
import com.vendo.core_lib.utils.AssertionUtils;
import com.vendo.security_lib.exception.ExceptionResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EmbeddedKafka
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InternalPresignControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PresignQueryPort presignQueryPort;

    @Nested
    class PresignedTests {

        @Test
        void presign_shouldReturnPresignUrl() throws Exception {
            FileRequest file = new FileRequest("id", "image/jpeg");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file));
            PresignBody presignBody = new PresignBody(file.id(), "url", "products/uuid");
            ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);

            when(presignQueryPort.presign(eq(request.type()), fileCaptor.capture())).thenReturn(presignBody);

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            File captorValue = fileCaptor.getValue();
            assertThat(captorValue).isNotNull();
            AssertionUtils.assertFrom(file, captorValue);

            PresignResponse presignResponse = objectMapper.readValue(content, PresignResponse.class);
            assertThat(presignResponse.data()).isNotNull();
            assertThat(presignResponse.data().size()).isEqualTo(1);
            assertThat(presignResponse.data().get(0)).isEqualTo(presignBody);

            verify(presignQueryPort).presign(request.type(), captorValue);
        }

        @Test
        void presign_shouldReturnUnauthorized_whenNoToken() throws Exception {
            FileRequest file = new FileRequest("id", "image/jpeg");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file));

            String content = mockMvc.perform(post("/internal/presign")
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(401);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenInvalidEnumType() throws Exception {
            String invalidBody = """
                    {"type": "invalid_type", "files": [{"id": "1", "contentType": "image/png"}]}
                    """;

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(invalidBody)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().get("type")).isEqualTo("Allowed types are: PRODUCT, CATEGORY");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenFilesAndTypeAreMissing() throws Exception {
            PresignRequest request = new PresignRequest(null, null);

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(2);
            assertThat(exceptionResponse.getErrors().get("type")).isEqualTo("Type is required.");
            assertThat(exceptionResponse.getErrors().get("files")).isEqualTo("At least 1 file is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenInvalidFiles() throws Exception {
            FileRequest file1 = new FileRequest(null, "image/jpeg");
            FileRequest file2 = new FileRequest("id2", null);
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file1, file2));

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(2);
            assertThat(exceptionResponse.getErrors().get("files[0].id")).isEqualTo("Id is required.");
            assertThat(exceptionResponse.getErrors().get("files[1].contentType")).isEqualTo("Content type is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenFileTypeIsNotImage() throws Exception {
            FileRequest file = new FileRequest("id", "video/mp4");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file));

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().get("contentType")).isEqualTo("Invalid file type of image: %s.".formatted(file.contentType()));
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenInvalidImageExtension() throws Exception {
            FileRequest file = new FileRequest("id", "image/jjppeegg");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file));

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(400);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Validation failed.");
            assertThat(exceptionResponse.getErrors()).isNotNull();
            assertThat(exceptionResponse.getErrors().get("contentType")).isEqualTo("Invalid file type of image: %s.".formatted(file.contentType()));
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnBadRequest_whenNotUniqueFileIds() throws Exception {
            FileRequest file1 = new FileRequest("id", "image/jpeg");
            FileRequest file2 = new FileRequest("id", "image/jpeg");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file1, file2));

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getTimestamp()).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(409);
            assertThat(exceptionResponse.getMessage()).isEqualTo("File ids must be unique.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presign_shouldReturnUnsupportedMediaType_whenContentTypeIsNotJson() throws Exception {
            FileRequest file = new FileRequest("id", "image/jpeg");
            PresignRequest request = new PresignRequest(ContextType.PRODUCT, List.of(file));

            String content = mockMvc.perform(post("/internal/presign")
                            .with(authentication(SecurityContextTestService.initializeEmptyAuth()))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.TEXT_PLAIN))
                    .andExpect(status().isUnsupportedMediaType())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            ExceptionResponse exceptionResponse = objectMapper.readValue(content, ExceptionResponse.class);
            assertThat(exceptionResponse).isNotNull();
            assertThat(exceptionResponse.getCode()).isEqualTo(415);
            assertThat(exceptionResponse.getMessage()).isEqualTo("Unsupported media type.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/internal/presign");

            verifyNoInteractions(presignQueryPort);
        }
    }
}
