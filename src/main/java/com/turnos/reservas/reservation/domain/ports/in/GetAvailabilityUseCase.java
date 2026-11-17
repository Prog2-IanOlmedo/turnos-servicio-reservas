package com.turnos.reservas.reservation.domain.ports.in;

import java.time.LocalDate;

import com.turnos.reservas.reservation.domain.model.Availability;

public interface GetAvailabilityUseCase {
    Availability getAvailability(Long professionalId, LocalDate date);
}
