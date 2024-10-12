package com.edwdev.samoyed.firmador.controllers;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.edwdev.samoyed.firmador.dto.ApiResponse;
import com.edwdev.samoyed.firmador.dto.EncryptRequestDTO;
import com.edwdev.samoyed.firmador.dto.FirmarDocumentoDTO;
import com.edwdev.samoyed.firmador.enums.TokensAvailables;
import com.edwdev.samoyed.firmador.exceptions.GenericException;
import com.edwdev.samoyed.firmador.services.SecurityServiceImpl;
import com.edwdev.samoyed.firmador.services.XAdESBESSignature;

@RestController
@RequestMapping("/firmador-digital/api")
public class FirmaController {

	@Autowired
	XAdESBESSignature xadesBesFirma;
	
	@Autowired
	SecurityServiceImpl securityServiceImpl;
	
	@PostMapping("/firmar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> firmarDocumento(
		@Valid @RequestBody FirmarDocumentoDTO firmarDocumentoDTO
    ) { 
		byte[] xmlByes = null;
		byte[] signatureBytes = null;
		Map<String, String> errors =new HashMap<>();
		try {	
			xmlByes = Base64.getDecoder().decode(firmarDocumentoDTO.getDocumentXml());
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			throw new GenericException("Error al decodificar el documento XML", 422, errors);
		}
		try {	
			signatureBytes = Base64.getDecoder().decode(firmarDocumentoDTO.getSignatureFile());
		} catch (Exception e) {
			errors.put("error", e.getMessage());
			throw new GenericException("Error al decodificar la firma digital", 422, errors);
		}
        
        if(xmlByes == null || xmlByes.length == 0) {
        	throw new GenericException("No ha enviado el archivo xml", 422,null);
        }
        if(signatureBytes == null || signatureBytes.length == 0) {
        	throw new GenericException("No ha enviado la firma en base64", 422, null);
        }
        if(firmarDocumentoDTO.getPassword() == null || firmarDocumentoDTO.getPassword().isEmpty()) {
        	throw new GenericException("No ha enviado la contraseña", 422, null);
        }
        
        firmarDocumentoDTO.setDocumentXmlInputStream(new ByteArrayInputStream(xmlByes));
        firmarDocumentoDTO.setSignatureInputStream(new ByteArrayInputStream(signatureBytes));
        if(firmarDocumentoDTO.isPasswordEncrypt()) {
        	if(firmarDocumentoDTO.getIv() == null || firmarDocumentoDTO.getIv().isEmpty()) {
        		throw new GenericException("Debe enviar el valor para el parámetro iv para desencriptar la contraseña", 422, null);
        	}
        	try {
        		String password = this.securityServiceImpl.decryptPassword(firmarDocumentoDTO.getPassword(), firmarDocumentoDTO.getIv());
        		firmarDocumentoDTO.setPassword(password);
			} catch (Exception e) {
				errors.put("error", e.getMessage());
            	throw new GenericException("Error al desencriptar la contraseña", 400, errors);
			}
        }
        
        Map<String, Object> result = xadesBesFirma.sign(firmarDocumentoDTO, TokensAvailables.BCE_IKEY2032);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>("Documento firmado exitosamente", result, null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
    @PostMapping("/encrypt-password")
    public ResponseEntity<Object> encryptPassword(@Valid @RequestBody EncryptRequestDTO request) throws Exception {
        String[] result = this.securityServiceImpl.encryptPassword(request.getPassword());
        Map<String, Object> payload = new HashMap<>();
        payload.put("encryptedPassword", result[0]);
        payload.put("iv", result[1]);
        ApiResponse<Map<String, Object>> response = new ApiResponse<>("Contraseña encriptada", payload , null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
