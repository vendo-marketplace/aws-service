package com.vendo.aws_service.service;

import com.vendo.aws_service.common.exceptions.S3UploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public void uploadFile(String fileName, byte[] fileData) {

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(fileData));
        } catch (S3Exception e) {
            throw new S3UploadException("Failed to upload file to S3:" + fileName);
        } catch (Exception e) {
            throw new S3UploadException("Internal service error while processing the file:" + fileName);
        }
    }
}
