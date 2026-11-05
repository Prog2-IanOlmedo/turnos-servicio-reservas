package com.turnos.reservas.user.domain.ports.out;

// El dominio no sabe con qué algoritmo se guarda la clave (hoy Argon2id).
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}
