package com.vendo.aws_service.adapter.aws.out;

import com.vendo.aws_service.adapter.aws.out.config.AwsProps;
import com.vendo.aws_service.adapter.aws.out.exception.AwsException;
import com.vendo.aws_service.adapter.file.out.FileExtensionParser;
import com.vendo.aws_service.domain.presign.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.presign.dto.PresignBody;
import com.vendo.aws_service.port.presign.PresignQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

import static com.vendo.core_lib.constants.Separators.SLASH_SEPARATOR;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsFileQueryAdapter implements PresignQueryPort {

    private final AwsProps props;
    private final S3Presigner s3Client;

    private final FileExtensionParser extensionParser;

    @Override
    public PresignBody presign(ContextType type, File file) {
        String key = type.getPath() + SLASH_SEPARATOR + generateFilename(file.contentType());
        String presignedUrl = generatePresignUrl(file.contentType(), key);

        return new PresignBody(
                file.id(),
                presignedUrl,
                key
        );
    }

    private String generatePresignUrl(String contentType, String key) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(props.getS3().getBucketName())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(props.getPresign().getExpiration()))
                .putObjectRequest(objectRequest)
                .build();

        try {
            PresignedPutObjectRequest presignedRequest = s3Client.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Something went wrong while presigning file: {}.", e.getMessage());
            throw new AwsException("Unable to presign file.");
        }
    }

    private String generateFilename(String contentType) {
        String extension = extensionParser.parse(contentType);
        return String.valueOf((UUID.randomUUID())).concat(extension);
    }
}