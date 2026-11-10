package com.turnos.reservas.reservation.application.exception;

public class InvalidPhoneNumberException extends RuntimeException {

    public InvalidPhoneNumberException(Throwable cause) {
        super("El teléfono tiene que tener entre 7 y 15 dígitos, opcionalmente con + adelante", cause);
    }
}
