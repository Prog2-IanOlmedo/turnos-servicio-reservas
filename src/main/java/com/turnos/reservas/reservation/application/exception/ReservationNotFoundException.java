package com.turnos.reservas.reservation.application.exception;

public class ReservationNotFoundException extends RuntimeException {

    private final Long reservationId;

    public ReservationNotFoundException(Long reservationId) {
        super("No existe la reserva con id: " + reservationId);
        this.reservationId = reservationId;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
