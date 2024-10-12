package com.edwdev.samoyed.firmador.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.InputStream;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FirmarDocumentoDTO {
	@NotNull(message = "El documento XML es obligatorio")
	private String documentXml;
	@NotNull(message = "La contraseña es obligatoria")
    private String password;
	@NotNull(message = "El campo passwordEncrypt es obligatorio")
    private boolean passwordEncrypt;
	@NotNull(message = "El archivo de firma es obligatorio")
    private String signatureFile;
	
    public String getIv() {
		return iv;
	}

	public void setIv(String iv) {
		this.iv = iv;
	}

	private String iv;
	
	
	private InputStream signatureInputStream;
	private InputStream documentXmlInputStream;
	
	
	// Getters y Setters

    public InputStream getSignatureInputStream() {
		return signatureInputStream;
	}

	public void setSignatureInputStream(InputStream signatureInputStream) {
		this.signatureInputStream = signatureInputStream;
	}

	public InputStream getDocumentXmlInputStream() {
		return documentXmlInputStream;
	}

	public void setDocumentXmlInputStream(InputStream documentXmlInputStream) {
		this.documentXmlInputStream = documentXmlInputStream;
	}

	public String getDocumentXml() {
        return documentXml;
    }

    public void setDocumentXml(String documentXml) {
        this.documentXml = documentXml;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordEncrypt() {
        return passwordEncrypt;
    }

    public void setPasswordEncrypt(boolean passwordEncrypt) {
        this.passwordEncrypt = passwordEncrypt;
    }

    public String getSignatureFile() {
        return signatureFile;
    }

    public void setSignatureFile(String signatureFile) {
        this.signatureFile = signatureFile;
    }
	
}
