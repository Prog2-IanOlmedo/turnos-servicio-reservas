package com.turnos.reservas.user.application.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String login) {
        super("No existe el usuario: " + login);
    }
}
