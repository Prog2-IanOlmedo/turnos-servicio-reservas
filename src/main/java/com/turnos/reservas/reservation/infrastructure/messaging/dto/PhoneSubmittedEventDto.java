package com.turnos.reservas.reservation.infrastructure.messaging.dto;

// AdditionalInformationSubmitted tal como lo pide la cátedra (contrato 15.5).
// Todos los campos son obligatorios.
public record PhoneSubmittedEventDto(
        String eventId,
        String eventType,
        String groupId,
        String reservationProcessId,
        String holdId,
        String requestEventId,
        Information information,
        String occurredAt,
        int schemaVersion) {

    public record Information(String phoneNumber) {
    }
}
