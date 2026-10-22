package com.turnos.reservas.reservation.domain.model;

// Estados locales del proceso de reserva. Las transiciones siguen el
// contrato de la cátedra: el estado nunca retrocede y los finales no se reabren.
public enum ReservationStatus {
    HELD,
    WAITING_FOR_PHONE,
    CONFIRMED,
    CANCELLED,
    EXPIRED,
    INVALID;

    public boolean isFinal() {
        return this == CANCELLED || this == EXPIRED || this == INVALID;
    }

    public boolean canMoveTo(ReservationStatus next) {
        return switch (this) {
            case HELD -> next == WAITING_FOR_PHONE || next == EXPIRED || next == INVALID;
            case WAITING_FOR_PHONE -> next == CONFIRMED || next == EXPIRED || next == INVALID;
            case CONFIRMED -> next == CANCELLED;
            case CANCELLED, EXPIRED, INVALID -> false;
        };
    }
}
