package com.renda.taskmanager.controller;

import com.renda.taskmanager.client.UserClient;
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
    public ResponseEntity<?> callUserViaFeign() {
        return userClient.hello();
    }

    @GetMapping("/hello-user")
    public String callUser() {
        return restTemplate.getForObject(
                "http://USER-SERVICE/api/users/hello", String.class);
    }

}
