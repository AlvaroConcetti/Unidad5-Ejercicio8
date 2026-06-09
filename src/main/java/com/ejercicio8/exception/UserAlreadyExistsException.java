package com.ejercicio8.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username) {
        super("El usuario '" + username + "' ya existe");
    }
}
