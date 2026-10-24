package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationEventType;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.ProcessedEventRepository;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplyReservationEventUseCaseImplTest {

    private static final String EVENT_ID = "confirmation-event-7d10";
    private static final String PROCESS_ID = "process-a92b47d0";

    @Mock
    private ReservationProcessRepository processRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private ApplyReservationEventUseCaseImpl useCase;

    private final ReservationEvent confirmacion = ReservationEvent.builder()
            .eventId(EVENT_ID)
            .type(ReservationEventType.APPOINTMENT_CONFIRMED)
            .reservationProcessId(PROCESS_ID)
            .reservationId(8251L)
            .build();

    private ReservationProcess procesoEn(ReservationStatus estado) {
        return ReservationProcess.builder().reservationProcessId(PROCESS_ID).status(estado).build();
    }

    @Test
    void ignoraUnEventoYaProcesado() {
        when(processedEventRepository.isProcessed(EVENT_ID)).thenReturn(true);

        assertThat(useCase.applyEvent(confirmacion)).isFalse();
        verify(processRepository, never()).findByReservationProcessId(anyString());
        verify(processedEventRepository, never()).markProcessed(anyString());
    }

    @Test
    void aplicaElEventoGuardaElProcesoYLoMarca() {
        ReservationProcess proceso = procesoEn(ReservationStatus.WAITING_FOR_PHONE);
        when(processRepository.findByReservationProcessId(PROCESS_ID)).thenReturn(Optional.of(proceso));

        assertThat(useCase.applyEvent(confirmacion)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(processRepository).save(proceso);
        verify(processedEventRepository).markProcessed(EVENT_ID);
    }

    @Test
    void unEventoTardioNoGuardaNadaPeroQuedaMarcado() {
        when(processRepository.findByReservationProcessId(PROCESS_ID))
                .thenReturn(Optional.of(procesoEn(ReservationStatus.EXPIRED)));

        assertThat(useCase.applyEvent(confirmacion)).isFalse();
        verify(processRepository, never()).save(any());
        verify(processedEventRepository).markProcessed(EVENT_ID);
    }

    @Test
    void unEventoDeUnProcesoDesconocidoSeMarcaYSeIgnora() {
        when(processRepository.findByReservationProcessId(PROCESS_ID)).thenReturn(Optional.empty());

        assertThat(useCase.applyEvent(confirmacion)).isFalse();
        verify(processRepository, never()).save(any());
        verify(processedEventRepository).markProcessed(EVENT_ID);
    }
}
