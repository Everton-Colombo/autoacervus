package com.example.autoacervus.exception;

public class LoginException extends RuntimeException {
    public LoginException() {}

    public LoginException(String message) {
        super(message);
    }
}
