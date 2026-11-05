package com.turnos.reservas.reservation.domain.ports.out;

import com.turnos.reservas.reservation.domain.model.PhoneSubmission;

// Manda la respuesta al pedido de teléfono. Hoy es Kafka, el dominio no lo sabe.
public interface PhoneSubmissionPublisher {

    void publish(PhoneSubmission submission);
}
