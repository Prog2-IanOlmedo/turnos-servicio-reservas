package com.turnos.reservas.reservation.infrastructure.client.dto;

// Body de POST /api/appointments/{reservationProcessId}/cancel (sección 12).
public record CatedraCancelRequestDto(String reason) {
}
