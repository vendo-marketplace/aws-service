package com.vendo.aws_service.adapter.file.out.kafka;

import com.vendo.aws_service.adapter.file.out.mapper.FileEventMapper;
import com.vendo.aws_service.domain.file.PresignedFile;
import com.vendo.aws_service.port.product.ProductImageEventSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductImageEventSenderAdapter implements ProductImageEventSenderPort {

    private final FileEventMapper mapper;
    private final ProductImageRequestedEventProducer producer;

    @Override
    public void send(PresignedFile presignedFile) {
        producer.send(mapper.toEvent(presignedFile));
    }
}
