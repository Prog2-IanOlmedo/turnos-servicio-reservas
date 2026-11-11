package com.turnos.reservas.reservation.domain.ports.out;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.Occupancy;

// Operaciones de turnos contra la cátedra (contrato, secciones 8 a 12).
public interface AppointmentGateway {

    List<Occupancy> findOccupancies(Long professionalId, LocalDate from, LocalDate to);

    Hold createHold(Long professionalId, LocalDate date, LocalTime startTime, String externalPatientId);

    // Devuelve el nuevo plazo para completar el paso del teléfono.
    Instant confirmHold(String holdId, String externalPatientId, String patientFirstName, String patientLastName);

    void cancel(String reservationProcessId, String reason);

    // Reservas de un paciente en la cátedra (sección 11), para reconciliar procesos.
    List<Appointment> findAppointments(String externalPatientId);
}
