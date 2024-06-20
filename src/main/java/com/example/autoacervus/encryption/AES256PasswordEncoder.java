package com.example.autoacervus.encryption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AES256PasswordEncoder implements PasswordEncoder {

    private static final AES256PasswordEncoder instance = new AES256PasswordEncoder();

    private AES256PasswordEncoder() {}

    public static AES256PasswordEncoder getInstance() { return instance; }

    @Override
    public String encode(CharSequence rawPassword) {
        String salt = AES256.generateSalt();
        String encryptedPassword = AES256.encrypt(rawPassword.toString(), salt);

        return encryptedPassword + "{" + salt + "}";
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String password = extractPassword(rawPassword.toString());
        String salt = extractSalt(rawPassword.toString());

        String encryptedPassword = AES256.encrypt(password, salt);

        return encryptedPassword.equals(extractPassword(encodedPassword));
    }

    private String extractSalt(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf("{");
        if (start != 0) {
            return "";
        } else {
            int end = prefixEncodedPassword.indexOf("}", start);
            return end < 0 ? "" : prefixEncodedPassword.substring(start, end + 1);
        }
    }

    private String extractPassword(String prefixEncodedPassword) {
        return prefixEncodedPassword.substring(0, prefixEncodedPassword.indexOf("}"));
    }
}
