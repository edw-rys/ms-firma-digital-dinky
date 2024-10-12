package com.edwdev.samoyed.firmador.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class SecurityServiceImpl {
	 @Value("${encryption.secret-key}")
	private String SECRET_KEY;

    /**
     * Desencripta una contraseña encriptada usando AES/CBC.
     *
     * @param encryptedPassword La contraseña encriptada en formato base64.
     * @param iv               El vector de inicialización (IV) en formato base64.
     * @return La contraseña desencriptada como String.
     * @throws Exception Si ocurre algún error durante el proceso de desencriptación.
     */
    public String decryptPassword(String encryptedPassword, String iv) throws Exception {
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedPassword);

        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");

        IvParameterSpec ivParams = new IvParameterSpec(ivBytes);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);

        // Desencriptar el valor
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        
        // Convertir los bytes desencriptados a String
        return new String(decryptedBytes, "UTF-8");
    }
    
    
    /**
     * Encripta una contraseña usando AES/CBC.
     *
     * @param password La contraseña a encriptar.
     * @return Un array de dos elementos, donde el primer elemento es la contraseña encriptada en formato Base64
     *         y el segundo elemento es el vector de inicialización (IV) en formato Base64.
     * @throws Exception Si ocurre algún error durante el proceso de encriptación.
     */
    public String[] encryptPassword(String password) throws Exception {
        // Generar un nuevo IV
        byte[] iv = new byte[16]; // Tamaño del IV para AES es 16 bytes
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv); // Llenar el IV con bytes aleatorios

        byte[] secretKeyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");
        IvParameterSpec ivParams = new IvParameterSpec(iv);

        // Inicializar el cifrador para encriptar
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);

        // Encriptar la contraseña
        byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

        // Convertir los resultados a Base64
        String encryptedPassword = Base64.getEncoder().encodeToString(encryptedBytes);
        String ivBase64 = Base64.getEncoder().encodeToString(iv);

        // Retornar la contraseña encriptada y el IV en formato Base64
        return new String[]{encryptedPassword, ivBase64};
    }

}
