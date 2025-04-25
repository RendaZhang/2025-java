package com.renda.taskmanager.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
@Schema(name = "ErrorResponseDto", description = "Unified error response model")
public class ErrorResponseDto {

    @Schema(description = "HTTP status code", example = "400")
    private int status;
    @Schema(description = "Human-readable message", example = "validation failed")
    private String message;
    @Schema(description = "List of field-level errors (present when validation fails)")
    private List<FieldError> fieldErrors;

    @Data
    @AllArgsConstructor
    @Schema(name = "FieldError", description = "Error for a single field")
    public static class FieldError {
        @Schema(description = "Field name", example = "title")
        private String field;
        @Schema(description = "Error message", example = "must not be blank")
        private String message;
    }

}
