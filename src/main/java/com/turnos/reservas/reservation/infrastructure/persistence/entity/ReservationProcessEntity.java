package com.turnos.reservas.reservation.infrastructure.persistence.entity;

import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "reservation_process")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationProcessEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String reservationProcessId;
    private String holdId;
    private String externalPatientId;
    private Long professionalId;
    @Column(name = "appointment_date")
    private LocalDate date;
    private LocalTime startTime;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    private Instant expiresAt;
    private Long reservationId;
    private String statusReason;
}
