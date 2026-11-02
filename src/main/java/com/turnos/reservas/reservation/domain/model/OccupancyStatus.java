package com.turnos.reservas.reservation.domain.model;

// Si un turno figura en los dos estados, la cátedra informa CONFIRMED (sección 8).
public enum OccupancyStatus {
    HELD,
    CONFIRMED
}
