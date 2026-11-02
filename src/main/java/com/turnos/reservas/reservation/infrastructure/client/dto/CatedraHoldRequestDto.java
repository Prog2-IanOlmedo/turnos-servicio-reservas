package com.turnos.reservas.reservation.infrastructure.client.dto;

// Body de POST /api/appointment-holds (sección 9). Fecha y hora van como texto
// (yyyy-MM-dd y HH:mm) igual que en el ejemplo del contrato.
public record CatedraHoldRequestDto(Long professionalId, String date, String startTime, String externalPatientId) {
}
