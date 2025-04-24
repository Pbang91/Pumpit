package com.example.pumpit.global.util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class BCryptService {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String generateRecoveryCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        int groupCount = 4, groupSize = 8;

        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int g = 0; g < groupCount; g++) {
            if (g > 0) {
                code.append("-");
            }

            for (int i = 0; i < groupSize; i++)
                code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }

    public static String encode(String input) {
        return passwordEncoder.encode(input);
    }

    public static boolean matches(String input, String encodedHash) {
        return passwordEncoder.matches(input, encodedHash);
    }
}
