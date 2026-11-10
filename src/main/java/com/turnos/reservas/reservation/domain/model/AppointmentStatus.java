package com.turnos.reservas.reservation.domain.model;

import java.util.Arrays;
import java.util.Optional;

// Estados de una reserva del lado de la cátedra (contrato, sección 11).
public enum AppointmentStatus {
    PHONE_PENDING,
    CONFIRMED,
    CANCELLED,
    FAILED;

    // Un estado nuevo que agregue la cátedra se ignora.
    public static Optional<AppointmentStatus> parse(String value) {
        return Arrays.stream(values()).filter(status -> status.name().equals(value)).findFirst();
    }
}
