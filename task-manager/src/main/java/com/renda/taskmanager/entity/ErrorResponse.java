package com.renda.taskmanager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private List<FieldError> fieldErrors;

    @Data
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }

}
