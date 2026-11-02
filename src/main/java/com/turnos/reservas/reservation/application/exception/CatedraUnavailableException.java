package com.turnos.reservas.reservation.application.exception;

// La cátedra no respondió: 5xx, timeout o respuesta que no se pudo leer.
public class CatedraUnavailableException extends RuntimeException {

    public CatedraUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
