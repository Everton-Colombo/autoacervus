package com.example.autoacervus.encryption;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.logging.Logger;

public class AES256PasswordEncoder implements PasswordEncoder {

    // It may seem like this class could be implemented with only static methods. However, it can't, as to match
    // spring's PasswordEncoder interface method signatures. Therefore, it is made to be a singleton.
    // (That checks one of the assignment's requirements, so that's also cool.)
    private static final AES256PasswordEncoder instance = new AES256PasswordEncoder();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(AES256PasswordEncoder.class);

    private final Logger logger = Logger.getLogger(AES256PasswordEncoder.class.getName());

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
        logger.info("[matches()]: rawPassword: " + rawPassword + ", encodedPassword: " + encodedPassword);

        // Although it would be ideal to encode the received rawPassword instead of decoding the already encoded
        // password, the AES256 algorithm used encodes implements internal salting, meaning that encrypting the same
        // password with the same salt (our salt, external to the AES256 implementation) produces different outputs.
        String decryptedCorrectPassword = decode(encodedPassword);

        return decryptedCorrectPassword.equals(rawPassword.toString());
    }

    // Usually, PasswordEncoders shouldn't have a decode function, as one-way hashing should be used most of the time.
    // In the scope of this project, however, it does make sense, as the decoded passwords are needed to communicate
    // with the acervus api.
    public String decode(String encodedPassword) {
        logger.info("[decode()]: encodedPassword: " + encodedPassword);
        String encryptedPassword = extractPassword(encodedPassword);
        String salt = extractSalt(encodedPassword);


        return AES256.decrypt(encryptedPassword, salt);
    }

    private String extractSalt(String encodedPassword) {
        int start = encodedPassword.indexOf("{");
        int end = encodedPassword.indexOf("}", start);

        return end < 0 ? "" : encodedPassword.substring(start + 1, end);
    }

    private String extractPassword(String encodedPassword) {
        logger.info("[extractPassword()]: encodedPassword = " + encodedPassword);
        return encodedPassword.substring(0, encodedPassword.indexOf("{"));
    }
}
