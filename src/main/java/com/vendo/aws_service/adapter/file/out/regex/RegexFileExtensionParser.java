package com.vendo.aws_service.adapter.file.out.regex;

import com.vendo.aws_service.adapter.file.out.FileExtensionParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RegexFileExtensionParser implements FileExtensionParser {

    private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("^image/([a-z.+-]+)$");

    private static final Map<String, String> EXTENSIONS_BY_SUBTYPE = Map.ofEntries(
            Map.entry("jpeg", ".jpg"),
            Map.entry("png", ".png"),
            Map.entry("webp", ".webp"),
            Map.entry("bmp", ".bmp"),
            Map.entry("tiff", ".tiff"),
            Map.entry("svg+xml", ".svg"),
            Map.entry("x-icon", ".ico"),
            Map.entry("heic", ".heic"),
            Map.entry("heif", ".heif"),
            Map.entry("avif", ".avif"),
            Map.entry("apng", ".apng")
    );

    @Override
    public String parse(String contentType) {
        Matcher matcher = CONTENT_TYPE_PATTERN.matcher(contentType.toLowerCase(Locale.ROOT));

        if (!matcher.matches()) {
            log.error("Invalid content type: {}.", contentType);
            throw new IllegalArgumentException("Invalid content type: %s.".formatted(contentType));
        }

        String extension = EXTENSIONS_BY_SUBTYPE.get(matcher.group(1));
        if (extension == null) {
            log.error("Unsupported content type: {}.", contentType);
            throw new IllegalArgumentException("Unsupported content type: %s.".formatted(contentType));
        }

        return extension;
    }
}
