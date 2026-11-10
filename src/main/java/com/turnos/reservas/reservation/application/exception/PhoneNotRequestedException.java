package com.turnos.reservas.reservation.application.exception;

import com.turnos.reservas.reservation.domain.model.ReservationStatus;

// La reserva no está esperando un teléfono: la cátedra todavía no lo pidió,
// el plazo venció o el proceso ya terminó.
public class PhoneNotRequestedException extends RuntimeException {

    public PhoneNotRequestedException(Long id, ReservationStatus status) {
        super("La reserva " + id + " no está esperando un teléfono (estado " + status + ")");
    }
}
