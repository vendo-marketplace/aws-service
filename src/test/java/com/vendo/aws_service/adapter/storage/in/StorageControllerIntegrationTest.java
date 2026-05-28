package com.vendo.aws_service.adapter.storage.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.adapter.security.out.jwt.parser.TokenClaims;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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

    private TokenClaims buildTokenClaims(UserRole role) {
        return new TokenClaims("id", UserStatus.ACTIVE, List.of(role.name()), true);
    }

    @Nested
    class PresignedTests {

        @Test
        void presigned_shouldReturnPresignedUrl() throws Exception {
            File file = new File("id", 50_000L, "image/jpg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            PresignedBody presignedBody = new PresignedBody(file.id(), "url", "products/uuid");
            TokenClaims claims = buildTokenClaims(UserRole.USER);

            when(presignQueryPort.presign(request.type(), file)).thenReturn(presignedBody);

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
                            .content(objectMapper.writeValueAsString(request))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            assertThat(content).isNotBlank();

            PresignedResponse presignedResponse = objectMapper.readValue(content, PresignedResponse.class);
            assertThat(presignedResponse.files()).isNotNull();
            assertThat(presignedResponse.files().size()).isEqualTo(1);
            assertThat(presignedResponse.files().get(0)).isEqualTo(presignedBody);
        }

        @Test
        void void_presigned_shouldReturnUnauthorized_whenNoToken() throws Exception {
            File file = new File("id", 50_000L, "image/jpg");
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
            TokenClaims claims = buildTokenClaims(UserRole.USER);

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
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
        void presigned_shouldReturnBadRequest_whenFileSizeExceeded() throws Exception {
            File file1 = new File("id1", 10_000_000L, "image/jpg");
            File file2 = new File("id2", 7_000_000L, "image/jpg");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file1, file2));
            TokenClaims claims = buildTokenClaims(UserRole.USER);

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
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
            assertThat(exceptionResponse.getMessage()).isEqualTo("Max size of 8MB reached for file: %s.".formatted(file1.id()));
            assertThat(exceptionResponse.getErrors()).isNull();
            assertThat(exceptionResponse.getPath()).isEqualTo("/storage/presigned");

            verifyNoInteractions(presignQueryPort);
        }

        @Test
        void presigned_shouldReturnBadRequest_whenFileTypeIsNotImage() throws Exception {
            File file = new File("id", 1_000_000L, "video/mp4");
            PresignedRequest request = new PresignedRequest(ContextType.PRODUCT, List.of(file));
            TokenClaims claims = buildTokenClaims(UserRole.USER);

            String content = mockMvc.perform(post("/storage/presigned")
                            .with(authentication(SecurityContextService.initializeAuth(claims)))
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
    }
}
