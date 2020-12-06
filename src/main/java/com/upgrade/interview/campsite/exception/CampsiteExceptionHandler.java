package com.upgrade.interview.campsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CampsiteExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidInput(InvalidInputException invalidInputException) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.BAD_REQUEST, invalidInputException.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @ExceptionHandler(CampsiteAlreadyBookedException.class)
    public ResponseEntity<ExceptionResponse> handleCampsiteAlreadyBooked(CampsiteAlreadyBookedException exception) {
        ExceptionResponse response = new ExceptionResponse(HttpStatus.CONFLICT, exception.getMessage());
        return new ResponseEntity<>(response, response.getStatus());
    }
}
