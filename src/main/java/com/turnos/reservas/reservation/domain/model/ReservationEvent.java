package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEvent {
    private String eventId;
    private ReservationEventType type;
    private String reservationProcessId;
    private Long reservationId;
    private String reason;
    // Solo lo traen el pedido y el rechazo del teléfono.
    private Instant expiresAt;

    // Efecto del evento sobre el proceso. Devuelve false si no cambia nada.
    public boolean applyTo(ReservationProcess process) {
        return switch (type) {
            case ADDITIONAL_INFORMATION_REQUESTED -> process.registerPhoneRequest(eventId, expiresAt);
            case ADDITIONAL_INFORMATION_REJECTED -> process.rejectPhone(reason, expiresAt);
            case APPOINTMENT_CONFIRMED -> process.confirm(reservationId);
            case APPOINTMENT_PROCESS_EXPIRED -> process.expire(reason);
            case APPOINTMENT_PROCESS_INVALID -> process.invalidate(reason);
            case APPOINTMENT_CANCELLED -> process.cancel(reason);
        };
    }
}
