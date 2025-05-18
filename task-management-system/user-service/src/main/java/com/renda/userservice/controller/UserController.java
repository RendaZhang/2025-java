package com.renda.userservice.controller;

import com.renda.common.dto.CommonResponseDto;
import com.renda.common.util.ResponseUtils;
import com.renda.userservice.dto.UserRequestDto;
import com.renda.userservice.dto.UserResponseDto;
import com.renda.userservice.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Management", description = "Operations related to users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Value("${eureka.instance.instance-id}")
    private String instanceID;

    /* ---------- Read Endpoints ---------- */

    @GetMapping("/hello")
    public ResponseEntity<CommonResponseDto<String>> hello() {
        return ResponseUtils.success("Hello from " + instanceID);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> getUser(@PathVariable Long id) {
        return ResponseUtils.success(userService.findOne(id));
    }

    @GetMapping
    public ResponseEntity<CommonResponseDto<List<UserResponseDto>>> getUsers() {
        return ResponseUtils.success(userService.findAll());
    }

    /* ---------- Write Endpoints ---------- */

    @PostMapping
    public ResponseEntity<CommonResponseDto<UserResponseDto>> create(@RequestBody UserRequestDto req) {
        return ResponseUtils.success(userService.create(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponseDto<UserResponseDto>> update(@PathVariable Long id,
                                                                     @RequestBody UserRequestDto req) {
        return ResponseUtils.success(userService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponseDto<String>> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseUtils.success("User deleted successfully");
    }


}
