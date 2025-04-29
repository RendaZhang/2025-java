package com.renda.userservice.util;

import com.renda.userservice.dto.CommonResponseDto;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {

    public static <T> ResponseEntity<CommonResponseDto<T>> success(T data) {
        return ResponseEntity.ok(
                CommonResponseDto.<T>builder()
                        .status(200)
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    public static ResponseEntity<CommonResponseDto<Void>> success() {
        return ResponseEntity.ok(
                CommonResponseDto.<Void>builder()
                        .status(200)
                        .message("Success")
                        .build()
        );
    }

    public static ResponseEntity<CommonResponseDto<Void>> error(int status, String message) {
        return ResponseEntity.status(status).body(
                CommonResponseDto.<Void>builder()
                        .status(status)
                        .message(message)
                        .build()
        );
    }
}
