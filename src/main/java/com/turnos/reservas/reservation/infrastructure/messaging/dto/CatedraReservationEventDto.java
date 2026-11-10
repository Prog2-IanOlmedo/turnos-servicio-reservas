package com.turnos.reservas.reservation.infrastructure.messaging.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Campos que me sirven de los eventos del topic de acciones (contrato 15.4 a 15.10).
// El contrato permite sumar campos nuevos: se ignoran.
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraReservationEventDto(
        String eventId,
        String eventType,
        String reservationProcessId,
        Long reservationId,
        String reason,
        Instant expiresAt) {
}
