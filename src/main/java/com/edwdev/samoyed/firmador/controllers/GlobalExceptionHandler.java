package com.edwdev.samoyed.firmador.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.edwdev.samoyed.firmador.dto.ErrorResponseDTO;
import com.edwdev.samoyed.firmador.exceptions.GenericException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        ErrorResponseDTO errorResponse = new ErrorResponseDTO("Errores de validación", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
	@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericExceptions(GenericException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(ex.getMessage(), ex.getErrors());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
