package com.turnos.reservas.reservation.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Turnos que un profesional atiende un día de la semana, según el catálogo.
public record WeeklySlots(DayOfWeek dayOfWeek, List<LocalTime> slots) {

    public boolean isOn(LocalDate date) {
        return date.getDayOfWeek() == dayOfWeek;
    }
}
