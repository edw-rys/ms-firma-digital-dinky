package com.edwdev.samoyed.firmador.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EncryptRequestDTO {
	@NotNull
	@NotBlank
	private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
