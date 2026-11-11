package com.turnos.reservas.reservation.infrastructure.scheduling;

import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.application.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationRecoveryCheckTest {

    @Mock
    private ReservationService reservationService;

    @Test
    void habilitadoReconcilia() {
        when(reservationService.reconcileOverdueProcesses()).thenReturn(2);

        new ReservationRecoveryCheck(reservationService, true).check();

        verify(reservationService).reconcileOverdueProcesses();
    }

    @Test
    void sinCambiosTampocoFalla() {
        when(reservationService.reconcileOverdueProcesses()).thenReturn(0);

        assertThatCode(() -> new ReservationRecoveryCheck(reservationService, true).check())
                .doesNotThrowAnyException();
    }

    @Test
    void deshabilitadoNoHaceNada() {
        new ReservationRecoveryCheck(reservationService, false).check();

        verify(reservationService, never()).reconcileOverdueProcesses();
    }

    @Test
    void siLaCatedraNoRespondeSeReintentaDespues() {
        when(reservationService.reconcileOverdueProcesses())
                .thenThrow(new CatedraUnavailableException("sin respuesta", null));

        assertThatCode(() -> new ReservationRecoveryCheck(reservationService, true).check())
                .doesNotThrowAnyException();
    }
}
