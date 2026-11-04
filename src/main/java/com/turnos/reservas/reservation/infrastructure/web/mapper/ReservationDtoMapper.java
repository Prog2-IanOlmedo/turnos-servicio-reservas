package com.turnos.reservas.reservation.infrastructure.web.mapper;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.infrastructure.web.dto.StartReservationRequest;
import com.turnos.reservas.reservation.infrastructure.web.dto.ReservationResponse;
import org.springframework.stereotype.Component;

// holdId es un dato interno de la cátedra y el paciente ya viene en la URL:
// ninguno de los dos se expone.
@Component
public class ReservationDtoMapper {

    public ReservationResponse toResponse(ReservationProcess process) {
        if (process == null) {
            return null;
        }
        return ReservationResponse.builder()
                .id(process.getId())
                .reservationProcessId(process.getReservationProcessId())
                .professionalId(process.getProfessionalId())
                .date(process.getDate())
                .startTime(process.getStartTime())
                .status(process.getStatus())
                .expiresAt(process.getExpiresAt())
                .reservationId(process.getReservationId())
                .statusReason(process.getStatusReason())
                .build();
    }

    // El paciente sale de la URL (después saldrá del JWT), nunca del body.
    public ReservationRequest toRequest(String externalPatientId, StartReservationRequest body) {
        return new ReservationRequest(externalPatientId, body.getProfessionalId(), body.getDate(),
                body.getStartTime(), body.getPatientFirstName(), body.getPatientLastName());
    }
}
