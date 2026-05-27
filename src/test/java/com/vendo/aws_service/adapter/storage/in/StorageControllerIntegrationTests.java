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

import static org.mockito.Mockito.when;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StorageControllerIntegrationTests {

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
            assertThat(presignedResponse.files().get(0)).isEqualTo(file);
        }

        @Test
        void void_presigned_shouldReturnUnauthorized_whenNoToken() {

        }

        @Test
        void presigned_shouldReturnBadRequest_whenTypeIsMissing() {

        }

        @Test
        void presigned_shouldReturnBadRequest_whenFilesAreEmpty() {

        }

        @Test
        void presigned_shouldReturnBadRequest_whenFileSizeExceeded() {

        }

        @Test
        void presigned_shouldReturnBadRequest_whenFileTypeIsNotImage() {

        }

    }

}
