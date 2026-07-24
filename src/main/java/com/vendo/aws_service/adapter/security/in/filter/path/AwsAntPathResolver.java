package com.vendo.aws_service.adapter.security.in.filter.path;

import com.vendo.aws_service.shared.props.PathProps;
import com.vendo.security_lib.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class AwsAntPathResolver implements AntPathResolver {

    private final PathProps props;
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean isPermittedPath(String path) {
        return Arrays.stream(props.allPaths()).anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
