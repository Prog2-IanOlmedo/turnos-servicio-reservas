package com.turnos.reservas.user.infrastructure.security;

import java.time.Duration;
import java.time.Instant;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.out.TokenIssuer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

// Igual que JHipster: el usuario va en "sub" y los roles en el claim "auth".
@Component
public class JwtTokenIssuer implements TokenIssuer {

    public static final String AUTHORITIES_CLAIM = "auth";

    private final JwtEncoder jwtEncoder;
    private final Duration validity;
    private final Duration rememberMeValidity;

    public JwtTokenIssuer(JwtEncoder jwtEncoder,
                          @Value("${security.jwt.token-validity:1d}") Duration validity,
                          @Value("${security.jwt.remember-me-validity:30d}") Duration rememberMeValidity) {
        this.jwtEncoder = jwtEncoder;
        this.validity = validity;
        this.rememberMeValidity = rememberMeValidity;
    }

    @Override
    public String issue(User user, boolean rememberMe) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user.getLogin())
                .issuedAt(now)
                .expiresAt(now.plus(rememberMe ? rememberMeValidity : validity))
                .claim(AUTHORITIES_CLAIM, String.join(" ", user.getAuthorities()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS512).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
