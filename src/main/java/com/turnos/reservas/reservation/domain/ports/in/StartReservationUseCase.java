package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;

public interface StartReservationUseCase {
    ReservationProcess start(ReservationRequest request);
}
