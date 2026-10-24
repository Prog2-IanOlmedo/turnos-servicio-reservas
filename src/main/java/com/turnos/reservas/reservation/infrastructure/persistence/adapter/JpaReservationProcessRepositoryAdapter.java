package com.turnos.reservas.reservation.infrastructure.persistence.adapter;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import com.turnos.reservas.reservation.infrastructure.persistence.mapper.ReservationProcessMapper;
import com.turnos.reservas.reservation.infrastructure.persistence.repository.JpaReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaReservationProcessRepositoryAdapter implements ReservationProcessRepository {

    private final JpaReservationProcessRepository jpaRepository;
    private final ReservationProcessMapper mapper;

    @Override
    public ReservationProcess save(ReservationProcess process) {
        return mapper.toDomainModel(jpaRepository.save(mapper.toEntity(process)));
    }

    @Override
    public Optional<ReservationProcess> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomainModel);
    }

    @Override
    public Optional<ReservationProcess> findByReservationProcessId(String reservationProcessId) {
        return jpaRepository.findByReservationProcessId(reservationProcessId).map(mapper::toDomainModel);
    }

    @Override
    public List<ReservationProcess> findByExternalPatientId(String externalPatientId) {
        return jpaRepository.findByExternalPatientIdOrderByDateDescStartTimeDesc(externalPatientId).stream()
                .map(mapper::toDomainModel)
                .toList();
    }
}
