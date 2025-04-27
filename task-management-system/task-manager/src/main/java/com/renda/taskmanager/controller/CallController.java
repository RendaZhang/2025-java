package com.renda.taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallController {

    private final RestTemplate restTemplate;

    @GetMapping("/hello-user")
    public String callUser() {
        return restTemplate.getForObject(
                "http://USER-SERVICE/api/users/hello", String.class);
    }

}
