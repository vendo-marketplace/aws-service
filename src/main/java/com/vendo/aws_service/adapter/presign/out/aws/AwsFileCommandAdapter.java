package com.vendo.aws_service.adapter.presign.out.aws;

import com.vendo.aws_service.adapter.presign.out.aws.config.AwsProps;
import com.vendo.aws_service.port.file.FileCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Component
@RequiredArgsConstructor
class AwsFileCommandAdapter implements FileCommandPort {

    private final AwsProps props;
    private final S3Client s3Client;

    @Override
    public void delete(String fileKey) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(props.getS3().getBucketName())
                .key(fileKey)
                .build();

        s3Client.deleteObject(request);
    }
}
