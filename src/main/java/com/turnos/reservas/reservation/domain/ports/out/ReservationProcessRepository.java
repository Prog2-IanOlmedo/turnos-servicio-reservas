package com.turnos.reservas.reservation.domain.ports.out;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;

import java.util.Optional;

public interface ReservationProcessRepository {

    ReservationProcess save(ReservationProcess process);
    Optional<ReservationProcess> findById(Long id);
    Optional<ReservationProcess> findByReservationProcessId(String reservationProcessId);

}
