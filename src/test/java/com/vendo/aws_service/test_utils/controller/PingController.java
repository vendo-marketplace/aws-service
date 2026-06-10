package com.vendo.aws_service.test_utils.controller;

import com.vendo.aws_service.test_utils.dto.PingRequest;
import com.vendo.aws_service.test_utils.dto.PingResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ping")
public class PingController {

    @PostMapping("/pong")
    public PingResponse ping(@RequestBody PingRequest request) {
        return new PingResponse(request.content());
    }

    @GetMapping("/pong")
    public String ping() {
        return "pong";
    }

}
