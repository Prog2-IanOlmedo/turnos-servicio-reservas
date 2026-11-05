package com.turnos.reservas.user.application.exception;

public class LoginAlreadyUsedException extends RuntimeException {

    public LoginAlreadyUsedException() {
        super("El login ya está en uso");
    }
}
