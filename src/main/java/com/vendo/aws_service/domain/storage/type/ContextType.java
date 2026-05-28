package com.vendo.aws_service.domain.storage.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContextType {

    PRODUCT("products");

    private final String path;
}
