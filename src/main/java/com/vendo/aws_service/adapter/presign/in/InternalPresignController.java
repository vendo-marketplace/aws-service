package com.vendo.aws_service.adapter.presign.in;

import com.vendo.aws_service.adapter.file.out.mapper.FileDtoMapper;
import com.vendo.aws_service.adapter.presign.in.dto.PresignRequest;
import com.vendo.aws_service.adapter.presign.in.dto.PresignResponse;
import com.vendo.aws_service.domain.presign.dto.PresignBody;
import com.vendo.aws_service.port.presign.PresignUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal")
class InternalPresignController {

    private final FileDtoMapper mapper;
    private final PresignUseCase useCase;

    @PostMapping("/presign")
    ResponseEntity<PresignResponse> presign(@Valid @RequestBody PresignRequest request) {
        List<PresignBody> presignedBodies = useCase.presign(request.type(), mapper.toFiles(request.files()));
        return ResponseEntity.ok(PresignResponse.of(presignedBodies));
    }

}
