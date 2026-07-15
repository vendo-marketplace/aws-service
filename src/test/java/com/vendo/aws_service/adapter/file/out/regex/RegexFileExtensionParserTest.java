package com.vendo.aws_service.adapter.file.out.regex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegexFileExtensionParserTest {

    private final RegexFileExtensionParser parser = new RegexFileExtensionParser();

    @ParameterizedTest
    @CsvSource({
            "image/jpeg, .jpg",
            "image/png, .png",
            "image/gif, .gif",
            "image/webp, .webp",
            "image/bmp, .bmp",
            "image/tiff, .tiff",
            "image/svg+xml, .svg",
            "image/x-icon, .ico",
            "image/heic, .heic",
            "image/heif, .heif",
            "image/avif, .avif",
            "image/apng, .apng",
            "IMAGE/PNG, .png"
    })
    void parse_shouldReturnExtension_forSupportedContentType(String contentType, String expectedExtension) {
        assertThat(parser.parse(contentType)).isEqualTo(expectedExtension);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "image/unknown",
            "application/json",
            "image/",
            "image",
            "not-a-content-type"
    })
    void parse_shouldThrow_forUnsupportedOrMalformedContentType(String contentType) {
        assertThatThrownBy(() -> parser.parse(contentType))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void parse_shouldThrow_whenContentTypeContainsPathTraversalAttempt() {
        assertThatThrownBy(() -> parser.parse("image/../../etc/passwd"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
