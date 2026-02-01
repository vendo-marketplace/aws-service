package com.vendo.aws_service.adapter.out.aws.s3;

import com.vendo.aws_service.adapter.common.exceptions.StorageException;
import com.vendo.aws_service.port.aws.StoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements StoragePort {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public void upload(String fileName, byte[] content, String contentType) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(content));
            log.info("Successfully uploaded {} to S3 bucket {}", fileName, bucketName);

        } catch (S3Exception e) {
            log.error("AWS S3 Error: {}", e.awsErrorDetails().errorMessage());
            throw new StorageException("Failed to upload file to S3: " + fileName, e);
        } catch (Exception e) {
            log.error("Unexpected error during S3 upload", e);
            throw new StorageException("Internal error uploading: " + fileName, e);
        }
    }
}
