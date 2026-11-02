package com.turnos.reservas.reservation.infrastructure.client.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraConfirmResponseDto(String reservationProcessId, String status, Instant expiresAt) {
}
