package com.edwdev.samoyed.firmador.exceptions;

import java.util.Map;

public class GenericException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	private final String message;
    private final int httpStatusCode;
    private final Map<String, String> errors;

    public GenericException(String message, int httpStatusCode, Map<String, String> errors) {
        super(message);
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }
    public Map<String, String> getErrors() {
        return errors;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
