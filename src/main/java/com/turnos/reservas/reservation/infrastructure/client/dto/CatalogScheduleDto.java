package com.turnos.reservas.reservation.infrastructure.client.dto;

import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Lo que uso de GET /api/professionals/{id}/schedules del catálogo.
@JsonIgnoreProperties(ignoreUnknown = true)
public record CatalogScheduleDto(String dayOfWeek, List<LocalTime> slots) {
}
