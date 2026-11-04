package com.turnos.reservas.user.infrastructure.web.dto;

import java.time.Instant;
import java.util.Set;

import lombok.*;

// Datos de la cuenta propia. El hash de la clave no sale nunca.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;
    private boolean activated;
    private String langKey;
    private Instant createdDate;
    private Set<String> authorities;
}
