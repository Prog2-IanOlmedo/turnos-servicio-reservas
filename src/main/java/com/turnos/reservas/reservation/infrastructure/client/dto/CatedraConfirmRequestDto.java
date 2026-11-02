package com.turnos.reservas.reservation.infrastructure.client.dto;

// Body de POST /api/appointment-holds/{holdId}/confirm (sección 10).
public record CatedraConfirmRequestDto(String externalPatientId, String patientFirstName, String patientLastName) {
}
