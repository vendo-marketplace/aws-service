package com.vendo.aws_service.adapter.storage.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.adapter.storage.in.dto.FileRequest;
import com.vendo.aws_service.adapter.storage.in.dto.PresignedRequest;
import com.vendo.aws_service.adapter.storage.in.dto.PresignedResponse;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;
import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.port.storage.PresignQueryPort;
import com.vendo.aws_service.test_utils.security.SecurityContextService;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import com.vendo.utils_lib.AssertionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StorageControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PresignQueryPort presignQueryPort;

    private User buildUser() {
        return User.builder()
                .id("id")
                .email("user@vendo.com")
                .status(UserStatus.ACTIVE)
                .roles(List.of(UserRole.USER.name()))
                .emailVerified(true)
                .build();
    }

    @Nested
    class PresignedTests {

        @Test
        void presigned_shouldReturnPresignedUrl() throws Exception {
            FileRequest file = new FileRequest("id", 50_000L, "image/jpeg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            PresignedBody presignedBody = new PresignedBody(file.id(), "url", "products/uuid");
            User user = buildUser();
            ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);

            when(presignQueryPort.presign(eq(request.type()), fileCaptor.capture())).thenReturn(presignedBody);

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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

            PresignedResponse presignedResponse = objectMapper.readValue(content, PresignedResponse.class);
            assertThat(presignedResponse.files()).isNotNull();
            assertThat(presignedResponse.files().size()).isEqualTo(1);
            assertThat(presignedResponse.files().get(0)).isEqualTo(presignedBody);

            verify(presignQueryPort).presign(request.type(), captorValue);
        }

        @Test
        void presigned_shouldReturnUnauthorized_whenNoToken() throws Exception {
            FileRequest file = new FileRequest("id", 50_000L, "image/jpeg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));

            String content = mockMvc.perform(post("/storage/presigned")
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
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenFilesAndTypeAreMissing() throws Exception {
            PresignedRequest request = new PresignedRequest(null, null);
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenInvalidFiles() throws Exception {
            FileRequest file1 = new FileRequest(null, 10_000_000L, "image/jpeg");
            FileRequest file2 = new FileRequest("id2", -1L, null);
            FileRequest file3 = new FileRequest("id3", 5_000_000L, "image/jpeg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file1, file2, file3));
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getErrors().size()).isEqualTo(4);
            assertThat(exceptionResponse.getErrors().get("files[0].id")).isEqualTo("Id is required.");
            assertThat(exceptionResponse.getErrors().get("files[0].size")).isEqualTo("Maximum allowed size is 8MB.");
            assertThat(exceptionResponse.getErrors().get("files[1].size")).isEqualTo("Minimum allowed size cannot be less than 1.");
            assertThat(exceptionResponse.getErrors().get("files[1].contentType")).isEqualTo("Content type is required.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenFileTypeIsNotImage() throws Exception {
            FileRequest file = new FileRequest("id", 1_000_000L, "video/mp4");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid file type of image: %s.".formatted(file.contentType()));
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenInvalidImageExtension() throws Exception {
            FileRequest file = new FileRequest("id", 1_000_000L, "image/jjppeegg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid file type of image: %s.".formatted(file.contentType()));
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenNotUniqueFileIds() throws Exception {
            FileRequest file1 = new FileRequest("id", 5_000_000L, "image/jpeg");
            FileRequest file2 = new FileRequest("id", 5_000_000L, "image/jpeg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file1, file2));
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getMessage()).isEqualTo("File ids must be unique.");
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnUnsupportedMediaType_whenContentTypeIsNotJson() throws Exception {
            FileRequest file = new FileRequest("id", 50_000L, "image/jpeg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            User user = buildUser();

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(user)))
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
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }
    }
}
