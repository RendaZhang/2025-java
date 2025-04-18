package com.renda.demo.exception;

public class AgeOutOfRangeException extends RuntimeException {
    public AgeOutOfRangeException(String message) {
        super(message);
    }
}
