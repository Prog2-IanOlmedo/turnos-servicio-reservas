package com.turnos.reservas.user.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Respuesta del login con el mismo nombre de campo que JHipster.
public record JwtTokenResponse(@JsonProperty("id_token") String idToken) {
}
