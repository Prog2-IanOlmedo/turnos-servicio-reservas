package com.turnos.reservas.reservation.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

// processed_at lo completa la base con su valor por defecto.
@Getter
@Setter
@Entity
@Table(name = "processed_event")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEventEntity {
    @Id
    private String eventId;
}
