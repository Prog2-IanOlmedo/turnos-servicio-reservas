package com.turnos.reservas.reservation.application.exception;

// La cátedra rechazó el pedido (4xx). code es el código estable del contrato
// (sección 13) y es lo que hay que mirar, nunca el texto del error.
// Puede venir null en los 401/403 que corta Spring Security.
public class CatedraRejectedException extends RuntimeException {

    private final int status;
    private final String code;

    public CatedraRejectedException(int status, String code) {
        super("La cátedra rechazó el pedido: HTTP " + status + (code != null ? " " + code : ""));
        this.status = status;
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public boolean hasCode(String expected) {
        return expected.equals(code);
    }
}
