package com.edwdev.samoyed.firmador.dto;

import java.util.List;

public class ApiResponse<T> {
    private String message;
    private T payload;
    private List<String> errors;

    public ApiResponse(String message, T payload, List<String> errors) {
        this.message = message;
        this.payload = payload;
        this.errors = errors;
    }

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
