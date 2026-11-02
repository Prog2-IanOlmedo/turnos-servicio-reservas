package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;

public interface CancelReservationUseCase {
    ReservationProcess cancel(ReservationProcess process, String reason);
}
