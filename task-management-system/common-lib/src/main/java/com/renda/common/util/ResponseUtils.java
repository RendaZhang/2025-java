package com.renda.common.util;

import com.renda.common.dto.CommonResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class ResponseUtils {

    public static <T> ResponseEntity<CommonResponseDto<T>> success(T data) {
        return ResponseEntity.ok(
                CommonResponseDto.<T>builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .data(data)
                        .build()
        );
    }

    public static <T> ResponseEntity<CommonResponseDto<T>> created(URI location, T data) {
        return ResponseEntity.created(location).body(
                CommonResponseDto.<T>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Created")
                        .data(data)
                        .build()
        );
    }

    public static ResponseEntity<CommonResponseDto<Void>> success() {
        return ResponseEntity.ok(
                CommonResponseDto.<Void>builder()
                        .status(HttpStatus.OK.value())
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
