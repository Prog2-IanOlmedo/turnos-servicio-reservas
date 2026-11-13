package com.turnos.reservas.reservation.application.usecases;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Occupancy;
import com.turnos.reservas.reservation.domain.model.OccupancyStatus;
import com.turnos.reservas.reservation.domain.model.WeeklySlots;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ProfessionalScheduleSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAvailabilityUseCaseImplTest {

    private static final LocalDate MARTES = LocalDate.of(2026, 11, 17);

    @Mock
    private ProfessionalScheduleSource scheduleSource;

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private GetAvailabilityUseCaseImpl useCase;

    private final WeeklySlots martes = new WeeklySlots(DayOfWeek.TUESDAY,
            List.of(LocalTime.of(11, 30), LocalTime.of(12, 0)));

    @Test
    void restaLasOcupacionesDeLaCatedra() {
        when(scheduleSource.findWeeklySlots(202L)).thenReturn(List.of(martes));
        when(appointmentGateway.findOccupancies(202L, MARTES, MARTES)).thenReturn(List.of(
                new Occupancy(MARTES, LocalTime.of(11, 30), OccupancyStatus.values()[0], null)));

        assertThat(useCase.getAvailability(202L, MARTES).freeSlots()).containsExactly(LocalTime.of(12, 0));
    }

    @Test
    void siNoAtiendeEseDiaNoConsultaALaCatedra() {
        when(scheduleSource.findWeeklySlots(202L)).thenReturn(List.of(martes));

        assertThat(useCase.getAvailability(202L, MARTES.plusDays(1)).freeSlots()).isEmpty();
        verify(appointmentGateway, never()).findOccupancies(any(), any(), any());
    }
}
