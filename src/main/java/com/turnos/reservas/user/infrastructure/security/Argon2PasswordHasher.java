package com.turnos.reservas.user.infrastructure.security;

import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

// Argon2id con los mínimos que recomienda OWASP: 19 MiB de memoria, 2 iteraciones
// y 1 hilo. Sal de 16 bytes y hash de 32. Cada hash lleva su propia sal.
@Component
public class Argon2PasswordHasher implements PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int PARALLELISM = 1;
    private static final int MEMORY_KIB = 19 * 1024;
    private static final int ITERATIONS = 2;

    private final Argon2PasswordEncoder encoder =
            new Argon2PasswordEncoder(SALT_LENGTH, HASH_LENGTH, PARALLELISM, MEMORY_KIB, ITERATIONS);

    @Override
    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return encoder.matches(rawPassword, passwordHash);
    }
}
