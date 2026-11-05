package com.turnos.reservas.user.domain.model;

import java.time.Instant;
import java.util.Set;

import lombok.*;

// Usuario final de la app. Sin @Setter ni toString: el hash no debe
// aparecer en ningún log.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public static final String ROLE_USER = "ROLE_USER";

    private Long id;
    private String login;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private boolean activated;
    private String langKey;
    private Instant createdDate;
    @Builder.Default
    private Set<String> authorities = Set.of();

    // Alta desde la app: queda activo de entrada y solo con ROLE_USER.
    // Estado y roles los decide el backend, no quien se registra (sección 3.2).
    public static User register(UserRegistration data, String passwordHash, Instant now) {
        return User.builder()
                .login(data.login())
                .passwordHash(passwordHash)
                .firstName(data.firstName())
                .lastName(data.lastName())
                .email(data.email())
                .imageUrl(data.imageUrl())
                .langKey(data.langKey())
                .activated(true)
                .authorities(Set.of(ROLE_USER))
                .createdDate(now)
                .build();
    }
}
