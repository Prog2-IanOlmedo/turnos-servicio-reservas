package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationEvent;

public interface ApplyReservationEventUseCase {
    boolean applyEvent(ReservationEvent event);
}
