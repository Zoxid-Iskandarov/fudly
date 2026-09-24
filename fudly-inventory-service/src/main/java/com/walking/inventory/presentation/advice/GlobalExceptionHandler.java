package com.walking.inventory.presentation.advice;

import com.walking.inventory.application.dto.common.ErrorResponse;
import com.walking.inventory.domain.exception.InsufficientStockException;
import com.walking.inventory.domain.exception.InvalidReservationOperationException;
import com.walking.inventory.domain.exception.ReservationNotFoundException;
import com.walking.inventory.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(
            InsufficientStockException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(
            ReservationNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            OptimisticLockingFailureException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Concurrent modification detected. Please retry.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .reduce("%s; %s"::formatted)
                .orElse("Validation error");
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = "Failed to convert parameter '%s'".formatted(e.getName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(InvalidReservationOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidReservationOperationException(
            InvalidReservationOperationException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, e.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }
}
