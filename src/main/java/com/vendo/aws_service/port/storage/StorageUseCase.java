package com.vendo.aws_service.port.storage;

import com.vendo.aws_service.domain.storage.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;

import java.util.List;

public interface StorageUseCase {

    List<PresignedBody> presign(ContextType type, List<File> files);

}
