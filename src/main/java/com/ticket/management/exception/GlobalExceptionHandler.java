package com.ticket.management.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ticket.management.dto.ErrorRecordDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;   
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.HashMap;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRecordDto> handleException(Exception exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "INTERNAL_SERVER_ERROR",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest webRequest) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        Map<String, String> errorMap = new HashMap<>();
        fieldErrors.forEach(fieldError -> errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorRecordDto> handleAuthenticationException(AuthenticationException exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "UNAUTHORIZED",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorRecordDto> handleAccessDeniedException(AccessDeniedException exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "FORBIDDEN",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorRecordDto> handleJwtException(JwtException exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "UNAUTHORIZED",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorRecordDto> handleSignatureException(SignatureException exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "UNAUTHORIZED",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorRecordDto> handleResourceConflictException(ResourceConflictException exception, WebRequest webRequest) {
        ErrorRecordDto errorRecordDto = new ErrorRecordDto(
            webRequest.getDescription(false),
            exception.getMessage(),
            "CONFLICT",
            LocalDateTime.now()
        );
        return new ResponseEntity<>(errorRecordDto,HttpStatus.CONFLICT);
    }
}
