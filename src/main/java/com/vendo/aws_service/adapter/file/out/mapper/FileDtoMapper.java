package com.vendo.aws_service.adapter.file.out.mapper;

import com.vendo.aws_service.adapter.storage.in.dto.FileRequest;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface FileDtoMapper {

    List<File> toFiles(List<FileRequest> requests);

}
