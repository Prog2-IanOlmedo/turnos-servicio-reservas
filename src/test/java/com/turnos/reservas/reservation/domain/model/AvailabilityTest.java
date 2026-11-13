package com.turnos.reservas.reservation.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityTest {

    private static final LocalDate MARTES = LocalDate.of(2026, 11, 17);
    private static final LocalDate MIERCOLES = LocalDate.of(2026, 11, 18);

    private final WeeklySlots martes = new WeeklySlots(DayOfWeek.TUESDAY,
            List.of(LocalTime.of(12, 0), LocalTime.of(11, 30), LocalTime.of(12, 30)));
    private final WeeklySlots jueves = new WeeklySlots(DayOfWeek.THURSDAY, List.of(LocalTime.of(18, 0)));

    private Occupancy ocupado(LocalDate fecha, LocalTime hora) {
        return new Occupancy(fecha, hora, OccupancyStatus.values()[0], null);
    }

    @Test
    void devuelveLosTurnosLibresDelDiaOrdenados() {
        Availability disponibilidad = Availability.of(202L, MARTES, List.of(martes, jueves),
                List.of(ocupado(MARTES, LocalTime.of(12, 0))));

        assertThat(disponibilidad.freeSlots()).containsExactly(LocalTime.of(11, 30), LocalTime.of(12, 30));
        assertThat(disponibilidad.professionalId()).isEqualTo(202L);
        assertThat(disponibilidad.date()).isEqualTo(MARTES);
    }

    @Test
    void unaOcupacionDeOtraFechaNoMolesta() {
        Availability disponibilidad = Availability.of(202L, MARTES, List.of(martes),
                List.of(ocupado(MIERCOLES, LocalTime.of(12, 0))));

        assertThat(disponibilidad.freeSlots()).hasSize(3);
    }

    @Test
    void sinAgendaEseDiaNoHayTurnos() {
        assertThat(Availability.of(202L, MIERCOLES, List.of(martes, jueves), List.of()).freeSlots()).isEmpty();
    }

    @Test
    void reconoceElDiaDeLaSemana() {
        assertThat(martes.isOn(MARTES)).isTrue();
        assertThat(martes.isOn(MIERCOLES)).isFalse();
    }
}
