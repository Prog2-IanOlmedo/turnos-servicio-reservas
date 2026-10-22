package com.turnos.reservas.reservation.infrastructure.persistence.mapper;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.infrastructure.persistence.entity.ReservationProcessEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationProcessMapper {

    public ReservationProcessEntity toEntity(ReservationProcess process) {
        if (process == null) {
            return null;
        }
        return ReservationProcessEntity.builder()
                .id(process.getId())
                .reservationProcessId(process.getReservationProcessId())
                .holdId(process.getHoldId())
                .externalPatientId(process.getExternalPatientId())
                .professionalId(process.getProfessionalId())
                .date(process.getDate())
                .startTime(process.getStartTime())
                .status(process.getStatus())
                .expiresAt(process.getExpiresAt())
                .reservationId(process.getReservationId())
                .statusReason(process.getStatusReason())
                .build();
    }

    public ReservationProcess toDomainModel(ReservationProcessEntity entity) {
        if (entity == null) {
            return null;
        }
        return ReservationProcess.builder()
                .id(entity.getId())
                .reservationProcessId(entity.getReservationProcessId())
                .holdId(entity.getHoldId())
                .externalPatientId(entity.getExternalPatientId())
                .professionalId(entity.getProfessionalId())
                .date(entity.getDate())
                .startTime(entity.getStartTime())
                .status(entity.getStatus())
                .expiresAt(entity.getExpiresAt())
                .reservationId(entity.getReservationId())
                .statusReason(entity.getStatusReason())
                .build();
    }
}
