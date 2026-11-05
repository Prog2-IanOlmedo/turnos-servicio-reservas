package com.turnos.reservas.user.application.exception;

public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException() {
        super("El email ya está en uso");
    }
}
