package com.renda.userservice.controller;

import com.renda.userservice.dto.CommonResponseDto;
import com.renda.userservice.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Value("${eureka.instance.instance-id}")
    private String instanceID;

    @GetMapping("/hello")
    public ResponseEntity<CommonResponseDto<String>> hello() {
        return ResponseUtils.success( "Hello from " + instanceID);
    }

}
