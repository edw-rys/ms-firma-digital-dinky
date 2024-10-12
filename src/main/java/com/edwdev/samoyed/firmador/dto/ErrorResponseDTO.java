package com.edwdev.samoyed.firmador.dto;

import java.util.Map;

public class ErrorResponseDTO {
	private String message;
    private Map<String, String> errors;
    
    public ErrorResponseDTO(String message, Map<String, String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

}
