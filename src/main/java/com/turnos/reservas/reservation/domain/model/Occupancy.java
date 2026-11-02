package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

// Un turno no disponible. expiresAt solo tiene valor cuando está HELD.
public record Occupancy(LocalDate date, LocalTime startTime, OccupancyStatus status, Instant expiresAt) {
}
