package com.vendo.aws_service.adapter.security.in.filter;

import com.vendo.aws_service.domain.user.User;
import com.vendo.aws_service.adapter.security.in.filter.header.UserHeadersExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserContextFilter extends OncePerRequestFilter {

    private final UserHeadersExtractor userHeadersExtractor;

    private final AwsAntPathResolver awsAntPathResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            User user = userHeadersExtractor.extract(request);
            FilterHelper.addAuthToContext(user, user.roles());
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            throw new AuthenticationServiceException("Internal authentication error.");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return awsAntPathResolver.isPermittedPath(request.getRequestURI());
    }
}