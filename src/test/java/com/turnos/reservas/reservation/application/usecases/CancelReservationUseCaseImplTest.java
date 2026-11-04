package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.application.exception.ReservationNotCancellableException;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelReservationUseCaseImplTest {

    @Mock
    private AppointmentGateway appointmentGateway;

    @Mock
    private ReservationProcessRepository reservationProcessRepository;

    @InjectMocks
    private CancelReservationUseCaseImpl useCase;

    private ReservationProcess procesoEn(ReservationStatus status) {
        return ReservationProcess.builder()
                .id(7L)
                .reservationProcessId("process-1")
                .externalPatientId("user-42")
                .status(status)
                .build();
    }

    @Test
    void cancelaUnaReservaConfirmada() {
        ReservationProcess process = procesoEn(ReservationStatus.CONFIRMED);
        when(reservationProcessRepository.save(process)).thenReturn(process);

        ReservationProcess cancelada = useCase.cancel(process, "Cambio de planes");

        verify(appointmentGateway).cancel("process-1", "Cambio de planes");
        assertThat(cancelada.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(cancelada.getStatusReason()).isEqualTo("Cambio de planes");
    }

    @Test
    void siYaEstabaCanceladaNoRepiteElPedido() {
        ReservationProcess process = procesoEn(ReservationStatus.CANCELLED);

        assertThat(useCase.cancel(process, "otra vez")).isSameAs(process);
        verify(appointmentGateway, never()).cancel(anyString(), any());
        verify(reservationProcessRepository, never()).save(any());
    }

    @Test
    void noSePuedeCancelarMientrasEsperaElTelefono() {
        ReservationProcess process = procesoEn(ReservationStatus.WAITING_FOR_PHONE);

        assertThatThrownBy(() -> useCase.cancel(process, null))
                .isInstanceOf(ReservationNotCancellableException.class)
                .hasMessageContaining("WAITING_FOR_PHONE");
        verify(appointmentGateway, never()).cancel(anyString(), any());
    }

    @Test
    void siLaCatedraFallaLaReservaSigueConfirmada() {
        ReservationProcess process = procesoEn(ReservationStatus.CONFIRMED);
        doThrow(new CatedraUnavailableException("sin respuesta", null))
                .when(appointmentGateway).cancel("process-1", null);

        assertThatThrownBy(() -> useCase.cancel(process, null)).isInstanceOf(CatedraUnavailableException.class);
        assertThat(process.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        verify(reservationProcessRepository, never()).save(any());
    }
}
