package com.turnos.reservas.reservation.infrastructure.persistence.adapter;

import com.turnos.reservas.reservation.domain.ports.out.ProcessedEventRepository;
import com.turnos.reservas.reservation.infrastructure.persistence.entity.ProcessedEventEntity;
import com.turnos.reservas.reservation.infrastructure.persistence.repository.JpaProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final JpaProcessedEventRepository jpaRepository;

    @Override
    public boolean isProcessed(String eventId) {
        return jpaRepository.existsById(eventId);
    }

    @Override
    public void markProcessed(String eventId) {
        jpaRepository.save(ProcessedEventEntity.builder().eventId(eventId).build());
    }
}
