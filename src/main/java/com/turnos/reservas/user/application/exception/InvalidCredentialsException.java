package com.turnos.reservas.user.application.exception;

// Mismo mensaje si el usuario no existe o si la clave está mal,
// para no revelar qué logins están registrados.
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Usuario o contraseña incorrectos");
    }
}
