package com.turnos.reservas.user.infrastructure.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class Argon2PasswordHasherTest {

    private final Argon2PasswordHasher hasher = new Argon2PasswordHasher();

    @Test
    void usaArgon2idYNoGuardaLaClave() {
        String hash = hasher.hash("secreto123");

        assertThat(hash).startsWith("$argon2id$").doesNotContain("secreto123");
    }

    @Test
    void reconoceLaClaveCorrectaYRechazaOtra() {
        String hash = hasher.hash("secreto123");

        assertThat(hasher.matches("secreto123", hash)).isTrue();
        assertThat(hasher.matches("otra-clave", hash)).isFalse();
    }

    @Test
    void laMismaClaveDaHashesDistintosPorLaSal() {
        assertThat(hasher.hash("secreto123")).isNotEqualTo(hasher.hash("secreto123"));
    }
}
