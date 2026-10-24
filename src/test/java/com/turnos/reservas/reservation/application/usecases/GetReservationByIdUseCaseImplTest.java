package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetReservationByIdUseCaseImplTest {

    @Mock
    private ReservationProcessRepository repository;

    @InjectMocks
    private GetReservationByIdUseCaseImpl useCase;

    @Test
    void buscaLaReservaPorSuId() {
        ReservationProcess reserva = ReservationProcess.builder().id(1L).build();
        when(repository.findById(1L)).thenReturn(Optional.of(reserva));

        assertThat(useCase.getReservationById(1L)).contains(reserva);
    }
}
