package com.renda.taskmanager.exception;

import com.renda.taskmanager.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleTaskNotFoundException(TaskNotFoundException e) {
        logger.warn("Task not found: {}", e.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        logger.warn("Entity not found: {}", e.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponseDto);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorResponseDto.FieldError> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(error ->
                        new ErrorResponseDto.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "validation failed",
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("Invalid argument: {}", e.getMessage());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDto);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
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

        logger.warn("{}: {}", message, rootMsg);

        ErrorResponseDto body = new ErrorResponseDto(
                status.value(),
                message,
                List.of()
        );

        return ResponseEntity.status(status).body(body);
    }

    /**
     * Fallback for all other exceptions
     *
     * @param e exception
     * @return a generic error message with HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception e) {
        logger.error("An unexpected error occurred: {}", e.getMessage(), e);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto);
    }

}
