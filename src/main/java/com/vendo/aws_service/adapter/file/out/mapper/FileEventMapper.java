package com.vendo.aws_service.adapter.file.out.mapper;

import com.vendo.aws_service.domain.file.PresignedFile;
import com.vendo.aws_service.infrastructure.mapper.MapStructConfig;
import com.vendo.event_lib.product.ProductImageRequestedEvent;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface FileEventMapper {

    ProductImageRequestedEvent toEvent(PresignedFile file);

}
