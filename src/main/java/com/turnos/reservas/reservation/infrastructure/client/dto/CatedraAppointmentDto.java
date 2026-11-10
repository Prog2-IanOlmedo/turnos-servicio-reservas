package com.turnos.reservas.reservation.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Lo que uso de cada reserva de GET /api/appointments (contrato, sección 11).
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraAppointmentDto(Long reservationId, String reservationProcessId, String externalPatientId,
                                    String status) {
}
