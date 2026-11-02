package com.turnos.reservas.reservation.infrastructure.client.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Respuesta de GET /api/appointment-occupancies (sección 8).
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatedraOccupanciesDto(
        Long professionalId,
        LocalDate from,
        LocalDate to,
        List<CatedraOccupancyDto> occupancies) {
}
