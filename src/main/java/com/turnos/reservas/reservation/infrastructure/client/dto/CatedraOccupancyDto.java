package com.turnos.reservas.reservation.infrastructure.client.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraOccupancyDto(LocalDate date, LocalTime startTime, String status, Instant expiresAt) {
}
