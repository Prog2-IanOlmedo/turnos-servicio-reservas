package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReservationsByPatientUseCaseImplTest {

    @Mock
    private ReservationProcessRepository repository;

    @InjectMocks
    private GetReservationsByPatientUseCaseImpl useCase;

    @Test
    void devuelveLasReservasDelPaciente() {
        ReservationProcess reserva = ReservationProcess.builder().id(1L).externalPatientId("user-42").build();
        when(repository.findByExternalPatientId("user-42")).thenReturn(List.of(reserva));

        assertThat(useCase.getReservationsByPatient("user-42")).containsExactly(reserva);
    }
}
