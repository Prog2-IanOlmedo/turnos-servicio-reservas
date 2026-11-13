package com.turnos.reservas.reservation.domain.ports.out;

import java.util.List;

import com.turnos.reservas.reservation.domain.model.WeeklySlots;

// Agenda semanal de un profesional (la publica el servicio de catálogo).
public interface ProfessionalScheduleSource {
    List<WeeklySlots> findWeeklySlots(Long professionalId);
}
