package com.renda.common.exception;

import com.renda.common.dto.CommonResponseDto;
import com.renda.common.util.ResponseUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        return ResponseUtils.error(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        log.warn("Validation failed: {}", message);
        return ResponseUtils.error(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Invalid argument: {}", e.getMessage());
        return ResponseUtils.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponseDto<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootMsg = Optional.of(ex.getMostSpecificCause())
                .map(Throwable::getMessage)
                .orElse(ex.getMessage());

        HttpStatus status;
        String message;

        if (rootMsg != null && rootMsg.matches(".*(unique|uq_|duplicate|duplicate entry).*?")) {
            status = HttpStatus.CONFLICT;
            message = "Unique constraint violated";
        } else if (rootMsg != null && rootMsg.matches(".*(foreign key|fk_|referential).*?")) {
            status = HttpStatus.CONFLICT;
            message = "Foreign-key constraint violated";
        } else {
            status = HttpStatus.BAD_REQUEST;
            message = "Data integrity violation";
        }

        log.warn("{}: {}", message, rootMsg);
        return ResponseUtils.error(status.value(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponseDto<Void>> handleGenericException(Exception e) {
        log.error("Unexpected exception: {}", e.getMessage(), e);
        return ResponseUtils.error(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.");
    }

}
