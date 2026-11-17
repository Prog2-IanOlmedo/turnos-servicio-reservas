package com.turnos.reservas.reservation.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Turnos libres de un profesional en una fecha.
public record Availability(Long professionalId, LocalDate date, List<LocalTime> freeSlots) {

    // Los turnos de la agenda de ese día de la semana menos los que la cátedra informa
    // ocupados en esa fecha (contrato, sección 8). Cualquier ocupación cuenta, también
    // un hold: si ya venció, la cátedra deja de informarlo.
    public static Availability of(Long professionalId, LocalDate date, List<WeeklySlots> schedules,
                                  List<Occupancy> occupancies) {
        Set<LocalTime> taken = occupancies.stream()
                .filter(occupancy -> date.equals(occupancy.date()))
                .map(Occupancy::startTime)
                .collect(Collectors.toSet());
        List<LocalTime> free = schedules.stream()
                .filter(schedule -> schedule.isOn(date))
                .flatMap(schedule -> schedule.slots().stream())
                .filter(slot -> !taken.contains(slot))
                .distinct()
                .sorted()
                .toList();
        return new Availability(professionalId, date, free);
    }
}
