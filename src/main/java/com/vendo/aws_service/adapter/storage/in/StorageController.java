package com.vendo.aws_service.adapter.storage.in;

import com.vendo.aws_service.adapter.storage.in.dto.PresignedRequest;
import com.vendo.aws_service.adapter.storage.in.dto.PresignedResponse;
import com.vendo.aws_service.domain.storage.dto.PresignedBody;
import com.vendo.aws_service.port.storage.StorageUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/storage")
@PreAuthorize("@userSecurity.validateActivation(authentication)")
class StorageController {

    private final StorageUseCase useCase;

    @PostMapping("/presigned")
    ResponseEntity<PresignedResponse> presigned(@Valid @RequestBody PresignedRequest request) {
        List<PresignedBody> presignedBodies = useCase.presign(request.type(), request.files());
        return ResponseEntity.ok(PresignedResponse.of(presignedBodies));
    }

}
