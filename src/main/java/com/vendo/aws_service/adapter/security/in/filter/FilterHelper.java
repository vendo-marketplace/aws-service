package com.vendo.aws_service.adapter.security.in.filter;

import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@NoArgsConstructor
final class FilterHelper {

    static void addAuthToContext(Object principal, List<String> roles) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, toAuthorities(roles));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private static List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}