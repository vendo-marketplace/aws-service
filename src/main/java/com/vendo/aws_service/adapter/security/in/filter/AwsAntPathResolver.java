package com.vendo.aws_service.adapter.security.in.filter;

import com.vendo.aws_service.shared.props.PathProps;
import com.vendo.security_lib.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AwsAntPathResolver implements AntPathResolver {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final PathProps pathProps;

    @Override
    public boolean isPermittedPath(String path) {
        Set<String> PERMITTED_PATHS = Arrays.stream(pathProps.getAllPaths()).collect(Collectors.toSet());
        return PERMITTED_PATHS.stream().anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
