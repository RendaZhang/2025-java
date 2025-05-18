package com.renda.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Standard API Response")
public class CommonResponseDto<T> {

    @Schema(description = "HTTP status code")
    private int status;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response data (optional)")
    private T data;

}
