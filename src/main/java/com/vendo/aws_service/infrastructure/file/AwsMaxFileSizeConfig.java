package com.vendo.aws_service.infrastructure.file;

import com.vendo.aws_service.adapter.storage.out.aws.config.AwsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsMaxFileSizeConfig {

    private final AwsProps props;

    @Bean
    public long awsMaxFileSize() {
        return props.getFile().getMaxSize();
    }

}
