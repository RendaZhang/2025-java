package com.renda.taskmanager.controller;

import com.renda.common.util.ResponseUtils;
import com.renda.taskmanager.client.UserClient;
import com.renda.common.dto.CommonResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Calling User Service", description = "Operations related to invoking User Service")
@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallController {

    private final RestTemplate restTemplate;

    private final UserClient userClient;

    @GetMapping("/hello-user-feign")
    public ResponseEntity<CommonResponseDto<String>> callUserViaFeign() {
        return userClient.hello();
    }

    @GetMapping("/hello-user")
    public ResponseEntity<CommonResponseDto<String>> callUser() {
        return ResponseUtils.success(restTemplate.getForObject(
                "http://USER-SERVICE/api/users/hello", String.class));
    }

}
