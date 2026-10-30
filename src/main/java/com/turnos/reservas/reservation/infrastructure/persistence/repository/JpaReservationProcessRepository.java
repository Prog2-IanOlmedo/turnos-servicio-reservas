package com.turnos.reservas.reservation.infrastructure.persistence.repository;

import com.turnos.reservas.reservation.infrastructure.persistence.entity.ReservationProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaReservationProcessRepository extends JpaRepository<ReservationProcessEntity, Long> {

    Optional<ReservationProcessEntity> findByReservationProcessId(String reservationProcessId);

    List<ReservationProcessEntity> findByExternalPatientIdOrderByDateDescStartTimeDesc(String externalPatientId);

}
