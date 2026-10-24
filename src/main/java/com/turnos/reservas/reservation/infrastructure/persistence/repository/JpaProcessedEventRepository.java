package com.turnos.reservas.reservation.infrastructure.persistence.repository;

import com.turnos.reservas.reservation.infrastructure.persistence.entity.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaProcessedEventRepository extends JpaRepository<ProcessedEventEntity, String> {
}
