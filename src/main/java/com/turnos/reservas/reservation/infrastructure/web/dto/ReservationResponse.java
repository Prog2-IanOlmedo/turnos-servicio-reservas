package com.turnos.reservas.reservation.infrastructure.web.dto;

import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private Long id;
    private String reservationProcessId;
    private Long professionalId;
    private LocalDate date;
    private LocalTime startTime;
    private ReservationStatus status;
    private Instant expiresAt;
    private Long reservationId;
    private String statusReason;
}
