package com.vendo.aws_service.adapter.security.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.aws_service.adapter.security.out.jwt.parser.AuthenticationParser;
import com.vendo.aws_service.domain.user.User;
import com.vendo.security_lib.exception.response.ExceptionResponse;
import com.vendo.user_lib.type.UserRole;
import com.vendo.user_lib.type.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.vendo.security_lib.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.constants.AuthConstants.BEARER_PREFIX;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JwtAuthFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationParser authenticationParser;

    @Test
    void doInternal_shouldPassAuthorization_whenUserAlreadyAuthorized() throws Exception {
        User authUser = new User("id", UserStatus.ACTIVE, List.of(UserRole.USER), true);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authUser,
                null,
                null);

        MockHttpServletResponse response = mockMvc.perform(get("/ping").with(authentication(authToken)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        assertThat(responseContent).isEqualTo("pong");

        verifyNoInteractions(authenticationParser);
    }

    @Test
    void doFilter_shouldReturnUnauthorized_whenNoTokenInRequest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/ping"))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Unauthorized.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");

        verifyNoInteractions(authenticationParser);
    }

    @Test
    void doFilter_shouldReturnUnauthorized_whenTokenWithoutBearerPrefix() throws Exception {
        String token = "token";

        MockHttpServletResponse response = mockMvc.perform(get("/ping")
                        .header(AUTHORIZATION_HEADER, token))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Invalid token.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");

        verifyNoInteractions(authenticationParser);
    }

    @Test
    void doFilter_shouldReturnUnauthorized_whenTokenExpired() throws Exception {
        String expiredToken = "expired_token";

        when(authenticationParser.extract(expiredToken)).thenThrow(new BadCredentialsException("Token expired."));

        MockHttpServletResponse response = mockMvc.perform(get("/ping").header(AUTHORIZATION_HEADER, BEARER_PREFIX + expiredToken))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse();

        String responseContent = response.getContentAsString();
        assertThat(responseContent).isNotBlank();
        ExceptionResponse exceptionResponse = objectMapper.readValue(responseContent, ExceptionResponse.class);

        assertThat(exceptionResponse.getMessage()).isEqualTo("Token expired.");
        assertThat(exceptionResponse.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(exceptionResponse.getPath()).isEqualTo("/ping");

        verify(authenticationParser).extract(expiredToken);
    }
}
