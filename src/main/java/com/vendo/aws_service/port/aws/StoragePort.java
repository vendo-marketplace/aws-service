package com.vendo.aws_service.port.aws;

public interface StoragePort {
    void upload(String fileName, byte[] content, String contentType);
}