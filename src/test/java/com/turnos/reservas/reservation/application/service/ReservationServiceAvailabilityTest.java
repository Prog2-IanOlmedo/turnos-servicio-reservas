package com.turnos.reservas.reservation.application.service;

import java.time.LocalDate;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Availability;
import com.turnos.reservas.reservation.domain.ports.in.GetAvailabilityUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceAvailabilityTest {

    @Mock
    private GetAvailabilityUseCase getAvailabilityUseCase;

    @InjectMocks
    private ReservationService service;

    @Test
    void delegaEnElCasoDeUso() {
        LocalDate fecha = LocalDate.of(2026, 11, 17);
        Availability disponibilidad = new Availability(202L, fecha, List.of());
        when(getAvailabilityUseCase.getAvailability(202L, fecha)).thenReturn(disponibilidad);

        assertThat(service.getAvailability(202L, fecha)).isSameAs(disponibilidad);
    }
}
