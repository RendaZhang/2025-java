package com.renda.userservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${eureka.instance.instance-id}")
    private String instanceID;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from " + instanceID;
    }

}
