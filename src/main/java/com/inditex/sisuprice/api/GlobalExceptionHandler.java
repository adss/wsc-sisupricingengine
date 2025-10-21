package com.inditex.sisuprice.api;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, IllegalArgumentException.class, ConstraintViolationException.class})
    public ResponseEntity<Map<String, Object>> handleValidation(Exception ex, HttpServletRequest request) {
        List<String> messages = switch (ex) {
            case MethodArgumentNotValidException manv ->
                    manv.getBindingResult().getAllErrors().stream()
                            .map(error -> {
                                if (error instanceof FieldError fe) {
                                    return fe.getField() + ": " + fe.getDefaultMessage();
                                }
                                return error.getDefaultMessage();
                            })
                            .toList();
            case ConstraintViolationException cve ->
                    cve.getConstraintViolations().stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .toList();
            default -> List.of(ex.getMessage());
        };
        log.warn("validation error path={} messages={}", request.getRequestURI(), messages);
        Map<String, Object> body = getBadRequestMessage(request);
        body.put("messages", messages);
        return ResponseEntity.badRequest().body(body);
    }

    private Map<String, Object> getBadRequestMessage(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("path", request.getRequestURI());
        return body;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest request) {
        log.warn("missing parameter path={} param={} message={}", request.getRequestURI(), ex.getParameterName(), ex.getMessage());
        Map<String, Object> body = getBadRequestMessage(request);
        body.put("messages", List.of("Missing parameter: " + ex.getParameterName()));
        return ResponseEntity.badRequest().body(body);
    }
}