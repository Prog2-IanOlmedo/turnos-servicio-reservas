package com.turnos.reservas.reservation.infrastructure.client.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraHoldResponseDto(String holdId, String reservationProcessId, String status, Instant expiresAt) {
}
