package com.turnos.reservas.reservation.domain.model;

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

    // Efecto del evento sobre el proceso. Devuelve false si no cambia nada.
    public boolean applyTo(ReservationProcess process) {
        return switch (type) {
            case APPOINTMENT_CONFIRMED -> process.confirm(reservationId);
            case APPOINTMENT_PROCESS_EXPIRED -> process.expire(reason);
            case APPOINTMENT_PROCESS_INVALID -> process.invalidate(reason);
            case APPOINTMENT_CANCELLED -> process.cancel(reason);
            // Pedido o rechazo del teléfono: el proceso sigue esperando
            // el teléfono hasta expiresAt (contrato 15.7).
            case ADDITIONAL_INFORMATION_REQUESTED, ADDITIONAL_INFORMATION_REJECTED -> false;
        };
    }
}
