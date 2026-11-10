package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;

// Respuesta al pedido de teléfono (AdditionalInformationSubmitted, contrato 15.5).
// eventId es nuevo en cada envío: si la cátedra rechaza el teléfono se puede mandar otro.
public record PhoneSubmission(
        String eventId,
        String reservationProcessId,
        String holdId,
        String requestEventId,
        PhoneNumber phoneNumber,
        Instant occurredAt) {
}
