package com.turnos.reservas.reservation.infrastructure.messaging;

import java.util.Optional;

import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationEventType;
import com.turnos.reservas.reservation.infrastructure.messaging.dto.CatedraReservationEventDto;
import org.springframework.stereotype.Component;

// Vacío si el tipo es desconocido o faltan los ids: ese mensaje no se puede aplicar.
@Component
public class CatedraEventMapper {

    public Optional<ReservationEvent> toDomain(CatedraReservationEventDto dto) {
        if (dto == null || dto.eventId() == null || dto.reservationProcessId() == null) {
            return Optional.empty();
        }
        return ReservationEventType.fromContractName(dto.eventType())
                .map(type -> ReservationEvent.builder()
                        .eventId(dto.eventId())
                        .type(type)
                        .reservationProcessId(dto.reservationProcessId())
                        .reservationId(dto.reservationId())
                        .reason(dto.reason())
                        .expiresAt(dto.expiresAt())
                        .build());
    }
}
