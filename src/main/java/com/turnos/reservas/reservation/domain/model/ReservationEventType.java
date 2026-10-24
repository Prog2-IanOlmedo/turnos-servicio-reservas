package com.turnos.reservas.reservation.domain.model;

import java.util.Arrays;
import java.util.Optional;

// Tipos de evento que publica la cátedra, con su nombre exacto en el contrato.
public enum ReservationEventType {
    ADDITIONAL_INFORMATION_REQUESTED("AdditionalInformationRequested"),
    ADDITIONAL_INFORMATION_REJECTED("AdditionalInformationRejected"),
    APPOINTMENT_CONFIRMED("AppointmentConfirmed"),
    APPOINTMENT_PROCESS_EXPIRED("AppointmentProcessExpired"),
    APPOINTMENT_PROCESS_INVALID("AppointmentProcessInvalid"),
    APPOINTMENT_CANCELLED("AppointmentCancelled");

    private final String contractName;

    ReservationEventType(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    // Un tipo desconocido se ignora: el contrato puede sumar eventos nuevos.
    public static Optional<ReservationEventType> fromContractName(String name) {
        return Arrays.stream(values())
                .filter(type -> type.contractName.equals(name))
                .findFirst();
    }
}
