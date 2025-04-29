package com.renda.userservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommonResponseDto<T> {

    private int status;

    private String message;

    private T data;

}
