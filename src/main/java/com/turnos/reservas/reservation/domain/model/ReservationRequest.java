package com.turnos.reservas.reservation.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

// Lo que pide el usuario para reservar: el turno y sus datos para la confirmación
// inicial (contrato, secciones 9 y 10).
public record ReservationRequest(
        String externalPatientId,
        Long professionalId,
        LocalDate date,
        LocalTime startTime,
        String patientFirstName,
        String patientLastName) {
}
