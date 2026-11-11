package com.turnos.reservas.reservation.infrastructure.persistence.repository;

import com.turnos.reservas.reservation.infrastructure.persistence.entity.ReservationProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JpaReservationProcessRepository extends JpaRepository<ReservationProcessEntity, Long> {

    Optional<ReservationProcessEntity> findByReservationProcessId(String reservationProcessId);

    List<ReservationProcessEntity> findByExternalPatientIdOrderByDateDescStartTimeDesc(String externalPatientId);

    List<ReservationProcessEntity> findByStatusIn(Collection<ReservationStatus> statuses);

}
