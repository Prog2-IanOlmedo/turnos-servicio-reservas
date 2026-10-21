package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import lombok.*;

// Sin @Setter: el estado solo cambia por las transiciones de abajo.
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationProcess {
    private Long id;
    private String reservationProcessId;
    private String holdId;
    private String externalPatientId;
    private Long professionalId;
    private LocalDate date;
    private LocalTime startTime;
    private ReservationStatus status;
    private Instant expiresAt;
    private Long reservationId;
    private String statusReason;

    // Cada transición devuelve false si no aplica (evento duplicado,
    // tardío o fuera de orden): Kafka entrega "al menos una vez".
    public boolean markWaitingForPhone(Instant newExpiresAt) {
        boolean moved = moveTo(ReservationStatus.WAITING_FOR_PHONE);
        if (moved) {
            this.expiresAt = newExpiresAt;
        }
        return moved;
    }

    public boolean confirm(Long confirmedReservationId) {
        boolean moved = moveTo(ReservationStatus.CONFIRMED);
        if (moved) {
            this.reservationId = confirmedReservationId;
        }
        return moved;
    }

    public boolean expire(String reason) {
        return moveWithReason(ReservationStatus.EXPIRED, reason);
    }

    public boolean invalidate(String reason) {
        return moveWithReason(ReservationStatus.INVALID, reason);
    }

    public boolean cancel(String reason) {
        return moveWithReason(ReservationStatus.CANCELLED, reason);
    }

    // Un proceso todavía abierto cuyo plazo ya pasó.
    public boolean isOverdueAt(Instant now) {
        return (status == ReservationStatus.HELD || status == ReservationStatus.WAITING_FOR_PHONE)
                && expiresAt != null
                && !now.isBefore(expiresAt);
    }

    private boolean moveWithReason(ReservationStatus next, String reason) {
        boolean moved = moveTo(next);
        if (moved) {
            this.statusReason = reason;
        }
        return moved;
    }

    private boolean moveTo(ReservationStatus next) {
        if (status == next || !status.canMoveTo(next)) {
            return false;
        }
        this.status = next;
        return true;
    }
}
