package com.ghostload.api.shared.adapter.in.web;

import com.ghostload.api.administration.domain.exception.InvalidAdminCredentialsException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationStateException;
import com.ghostload.api.assessment.domain.exception.InvalidEvaluationTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

// Se agregaron 2 manejadores nuevos: para token inválido (401) y para
// transición de estado inválida (409), ambos definidos en el openapi.yaml.
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAdminCredentialsException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidCredentials(
            InvalidAdminCredentialsException exception,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_CREDENTIALS",
                exception.getMessage(),
                request.getRequestURI(),
                null,
                List.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        List<FieldErrorResponse> fields = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                .toList();
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Uno o más campos son inválidos.",
                request.getRequestURI(),
                null,
                fields));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST",
                exception.getMessage(),
                request.getRequestURI(),
                null,
                List.of()));
    }

    // NUEVO: token de evaluación inválido -> 401
    @ExceptionHandler(InvalidEvaluationTokenException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidEvaluationToken(
            InvalidEvaluationTokenException exception,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_EVALUATION_TOKEN",
                exception.getMessage(),
                request.getRequestURI(),
                null,
                List.of()));
    }

    // NUEVO: transición de estado inválida -> 409, tal como pide el openapi.yaml
    @ExceptionHandler(InvalidEvaluationStateException.class)
    ResponseEntity<ApiErrorResponse> handleInvalidEvaluationState(
            InvalidEvaluationStateException exception,
            HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.CONFLICT.value(),
                "INVALID_STATE_TRANSITION",
                exception.getMessage(),
                request.getRequestURI(),
                null,
                List.of()));
    }
}
