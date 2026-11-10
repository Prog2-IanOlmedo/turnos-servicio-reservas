package com.turnos.reservas.reservation.domain.model;

// Una reserva tal como la informa la cátedra en GET /api/appointments.
public record Appointment(String reservationProcessId, Long reservationId, AppointmentStatus status) {
}
