package com.vendo.aws_service.port.product;

import com.vendo.aws_service.domain.file.PresignedFile;

public interface ProductEventSenderPort {

    void sendImageRequested(PresignedFile presignedFile);

}
