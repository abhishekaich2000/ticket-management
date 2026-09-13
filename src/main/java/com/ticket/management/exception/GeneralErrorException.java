package com.ticket.management.exception;

import org.springframework.http.HttpStatus;

public class GeneralErrorException extends RuntimeException {
    HttpStatus httpStatus;
    String message;
    
    public GeneralErrorException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    public String getMessage() {
        return message;
    }
}
