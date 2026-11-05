package com.turnos.reservas.user.infrastructure.security;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

// JWT de los usuarios de la app, firmado con HS512. La clave sale de JWT_SECRET
// (Base64, mínimo 64 bytes) y la comparten los dos servicios para validar.
@Slf4j
@Configuration
public class JwtConfig {

    private static final String ALGORITHM = "HmacSHA512";
    private static final int MIN_KEY_BYTES = 64;
    private static final SecureRandom RANDOM = new SecureRandom();

    // Sin clave genero una al azar para que levanten los tests y el CI sin
    // secretos en el repo. Con una clave cargada pero corta, no arranco.
    @Bean
    SecretKey jwtSecretKey(@Value("${security.jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            log.warn("JWT_SECRET vacía: uso una clave al azar. Los tokens no sobreviven un reinicio.");
            byte[] random = new byte[MIN_KEY_BYTES];
            RANDOM.nextBytes(random);
            return new SecretKeySpec(random, ALGORITHM);
        }
        byte[] bytes = Base64.getDecoder().decode(secret.trim());
        if (bytes.length < MIN_KEY_BYTES) {
            throw new IllegalStateException("JWT_SECRET tiene que tener al menos " + MIN_KEY_BYTES + " bytes");
        }
        return new SecretKeySpec(bytes, ALGORITHM);
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS512).build();
    }
}
