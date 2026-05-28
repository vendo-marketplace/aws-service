package com.vendo.aws_service.adapter.storage.out.aws.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProps {

    private String accessKey;
    private String secretKey;
    private String region;

    private S3 s3;
    private Presign presign;
    private File file;

    @Getter
    @Setter
    public static class S3 {
        private String bucketName;
    }

    @Getter
    @Setter
    public static class Presign {
        private long expiration;
    }

    @Getter
    @Setter
    public static class File {
        private long maxSize;
    }
}
