package com.turnos.reservas.reservation.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// De los errores de la cátedra solo me interesa el code estable (sección 13).
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraProblemDto(Integer status, String code) {
}
