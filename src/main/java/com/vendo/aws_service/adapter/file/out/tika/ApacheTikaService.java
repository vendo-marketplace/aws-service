package com.vendo.aws_service.adapter.file.out.tika;

import com.vendo.aws_service.adapter.file.out.FileExtensionParser;
import com.vendo.aws_service.port.file.FileFormatValidationPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApacheTikaService implements FileExtensionParser, FileFormatValidationPort {

    private static final String IMAGE_PREFIX = "image/";

    @Override
    public String parse(String contentType) {
        try {
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType mimeType = allTypes.forName(contentType);
            return mimeType.getExtension();
        } catch (MimeTypeException e) {
            log.error("Invalid content type for: {}, message: {}.", contentType, e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public boolean isImage(String contentType) {
        return contentType.startsWith(IMAGE_PREFIX);
    }
}
