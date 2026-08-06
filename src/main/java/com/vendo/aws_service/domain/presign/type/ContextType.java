package com.vendo.aws_service.domain.presign.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContextType {

    PRODUCT("products"),
    CATEGORY("categories");

    private final String path;
}
