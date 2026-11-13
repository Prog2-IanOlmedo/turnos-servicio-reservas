package com.turnos.reservas.reservation.application.usecases;

import java.time.LocalDate;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Availability;
import com.turnos.reservas.reservation.domain.model.Occupancy;
import com.turnos.reservas.reservation.domain.model.WeeklySlots;
import com.turnos.reservas.reservation.domain.ports.in.GetAvailabilityUseCase;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ProfessionalScheduleSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Si el profesional no atiende ese día no hace falta preguntarle nada a la cátedra.
@Component
@RequiredArgsConstructor
public class GetAvailabilityUseCaseImpl implements GetAvailabilityUseCase {

    private final ProfessionalScheduleSource scheduleSource;
    private final AppointmentGateway appointmentGateway;

    @Override
    public Availability getAvailability(Long professionalId, LocalDate date) {
        List<WeeklySlots> schedules = scheduleSource.findWeeklySlots(professionalId);
        boolean worksThatDay = schedules.stream().anyMatch(schedule -> schedule.isOn(date));
        List<Occupancy> occupancies = worksThatDay
                ? appointmentGateway.findOccupancies(professionalId, date, date)
                : List.of();
        return Availability.of(professionalId, date, schedules, occupancies);
    }
}
