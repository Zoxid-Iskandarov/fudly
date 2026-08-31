package com.walking.listing.web.advice;

import com.walking.listing.domain.dto.common.ErrorResponse;
import com.walking.listing.domain.exception.InvalidListingOperationException;
import com.walking.listing.domain.exception.ListingNotEditableException;
import com.walking.listing.domain.exception.MerchantServiceUnavailableException;
import com.walking.listing.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tools.jackson.databind.exc.InvalidFormatException;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler(ListingNotEditableException.class)
    public ResponseEntity<ErrorResponse> handleListingNotEditableException(
            ListingNotEditableException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(InvalidListingOperationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidListingOperationException(
            InvalidListingOperationException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(MerchantServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleMerchantServiceUnavailableException(
            MerchantServiceUnavailableException e,
            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {
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
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {
        String name = e.getName();
        String type = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "unknown";
        Object value = e.getValue();
        String message = "Failed to convert parameter '%s' with value '%s' to required type '%s'"
                .formatted(name, value, type);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {
        String message = "Malformed JSON request or invalid enum value";
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String targetType = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "unknown";
            String invalidValue = String.valueOf(ife.getValue());
            message = "Invalid value '%s' for type '%s'".formatted(invalidValue, targetType);
        }

        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI());

        return ResponseEntity.status(status)
                .body(response);
    }
}
