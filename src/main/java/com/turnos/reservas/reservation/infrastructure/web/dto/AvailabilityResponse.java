package com.turnos.reservas.reservation.infrastructure.web.dto;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Availability;

// Horarios como "HH:mm", que es lo que muestra la app.
public record AvailabilityResponse(Long professionalId, String date, List<String> slots) {

    private static final DateTimeFormatter HOUR = DateTimeFormatter.ofPattern("HH:mm");

    public static AvailabilityResponse from(Availability availability) {
        return new AvailabilityResponse(availability.professionalId(), availability.date().toString(),
                availability.freeSlots().stream().map(HOUR::format).toList());
    }
}
