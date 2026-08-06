package com.vendo.aws_service.adapter.aws.out;

import com.vendo.aws_service.adapter.aws.out.config.AwsProps;
import com.vendo.aws_service.adapter.aws.out.exception.AwsException;
import com.vendo.aws_service.port.file.FileCommandPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;

@Slf4j
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

        try {
            DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(request);
            System.out.println(deleteObjectResponse.toString());
            System.out.println(deleteObjectResponse);
        } catch (SdkException e) {
            log.error("Unable to delete file: {}.", e.getMessage());
            throw new AwsException("Something went wrong while deleting file.");
        }
    }
}
