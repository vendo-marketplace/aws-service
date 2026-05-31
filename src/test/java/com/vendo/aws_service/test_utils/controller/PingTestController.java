package com.vendo.aws_service.test_utils.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ping")
public class PingTestController {

    @GetMapping
    public String pong() {
        return "pong";
    }

}
