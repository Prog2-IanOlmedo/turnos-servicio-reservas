package com.turnos.reservas.reservation.application.exception;

import com.turnos.reservas.reservation.domain.model.ReservationStatus;

public class ReservationNotCancellableException extends RuntimeException {

    public ReservationNotCancellableException(Long id, ReservationStatus status) {
        super("La reserva " + id + " no se puede cancelar en estado " + status);
    }
}
