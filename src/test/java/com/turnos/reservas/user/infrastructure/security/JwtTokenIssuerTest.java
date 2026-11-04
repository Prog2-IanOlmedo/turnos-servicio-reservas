package com.turnos.reservas.user.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;

import javax.crypto.SecretKey;

import com.turnos.reservas.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import java.time.temporal.ChronoUnit;

class JwtTokenIssuerTest {

    private final JwtConfig config = new JwtConfig();
    private final SecretKey clave = config.jwtSecretKey("");
    private final JwtDecoder decoder = config.jwtDecoder(clave);
    private final JwtTokenIssuer issuer =
            new JwtTokenIssuer(config.jwtEncoder(clave), Duration.ofDays(1), Duration.ofDays(30));

    private final User ian = User.builder().login("ian").authorities(Set.of(User.ROLE_USER)).build();

    @Test
    void elTokenLlevaElUsuarioYSusRoles() {
        Jwt jwt = decoder.decode(issuer.issue(ian, false));

        assertThat(jwt.getSubject()).isEqualTo("ian");
        assertThat(jwt.getClaimAsString(JwtTokenIssuer.AUTHORITIES_CLAIM)).isEqualTo(User.ROLE_USER);
        assertThat(jwt.getExpiresAt()).isCloseTo(Instant.now().plus(Duration.ofDays(1)), within(1, ChronoUnit.MINUTES));
    }

    @Test
    void recordarmeDuraMas() {
        Jwt jwt = decoder.decode(issuer.issue(ian, true));

        assertThat(jwt.getExpiresAt()).isCloseTo(Instant.now().plus(Duration.ofDays(30)), within(1, ChronoUnit.MINUTES));
    }

    @Test
    void unTokenFirmadoConOtraClaveNoSeAcepta() {
        String token = issuer.issue(ian, false);
        JwtDecoder otroDecoder = config.jwtDecoder(config.jwtSecretKey(""));

        assertThatThrownBy(() -> otroDecoder.decode(token)).isInstanceOf(Exception.class);
    }

    @Test
    void aceptaUnaClaveBase64DeAlMenos64Bytes() {
        String claveValida = Base64.getEncoder().encodeToString(new byte[64]);

        assertThat(config.jwtSecretKey(claveValida).getEncoded()).hasSize(64);
    }

    @Test
    void rechazaUnaClaveCorta() {
        String claveCorta = Base64.getEncoder().encodeToString(new byte[16]);

        assertThatThrownBy(() -> config.jwtSecretKey(claveCorta))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("64");
    }
}
