package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;

import java.util.Optional;

public interface GetReservationByIdUseCase {
    Optional<ReservationProcess> getReservationById(Long id);
}
