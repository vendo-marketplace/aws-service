package com.vendo.aws_service.port.presign;

import com.vendo.aws_service.domain.presign.type.ContextType;
import com.vendo.aws_service.domain.file.File;
import com.vendo.aws_service.domain.presign.dto.PresignBody;

public interface PresignQueryPort {

    PresignBody presign(ContextType type, File file);

}
