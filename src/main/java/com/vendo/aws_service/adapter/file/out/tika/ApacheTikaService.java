package com.vendo.aws_service.adapter.file.out.tika;

import com.vendo.aws_service.adapter.file.out.FileExtensionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApacheTikaService implements FileExtensionParser {

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
}
