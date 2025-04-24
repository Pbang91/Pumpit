package com.example.pumpit.global.util;

import com.example.pumpit.global.exception.CustomException;
import com.example.pumpit.global.exception.enums.CustomExceptionData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESCBCUtil {
    private final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private final String KEY;

    public AESCBCUtil(@Value("${security.aes.key}") String key) {
        this.KEY = key;
    }

    private SecretKeySpec getKeySpec() {
        byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }

    // 암호화
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, e.getMessage());
        }
    }

    // 복호화
    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = getKeySpec();

            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new CustomException(CustomExceptionData.INTERVAL_SERVER_ERROR, e.getMessage());
        }
    }
}
