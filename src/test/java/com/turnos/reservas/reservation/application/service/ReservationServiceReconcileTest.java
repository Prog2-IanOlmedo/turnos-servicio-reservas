package com.turnos.reservas.reservation.application.service;

import com.turnos.reservas.reservation.domain.ports.in.ReconcileOverdueProcessesUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceReconcileTest {

    @Mock
    private ReconcileOverdueProcessesUseCase reconcileOverdueProcessesUseCase;

    @InjectMocks
    private ReservationService service;

    @Test
    void delegaEnElCasoDeUso() {
        when(reconcileOverdueProcessesUseCase.reconcile()).thenReturn(3);

        assertThat(service.reconcileOverdueProcesses()).isEqualTo(3);
    }
}
