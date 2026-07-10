package com.vendo.aws_service.port.product;

import com.vendo.aws_service.domain.file.PresignedFile;

public interface ProductImageEventSenderPort {

    void send(PresignedFile presignedFile);

}
