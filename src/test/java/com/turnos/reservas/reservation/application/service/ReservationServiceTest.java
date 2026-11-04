package com.turnos.reservas.reservation.application.service;

import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.ApplyReservationEventUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationByIdUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationsByPatientUseCase;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.domain.ports.in.CancelReservationUseCase;
import com.turnos.reservas.reservation.domain.ports.in.StartReservationUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private GetReservationsByPatientUseCase getReservationsByPatientUseCase;

    @Mock
    private GetReservationByIdUseCase getReservationByIdUseCase;

    @Mock
    private ApplyReservationEventUseCase applyReservationEventUseCase;

    @Mock
    private StartReservationUseCase startReservationUseCase;

    @Mock
    private CancelReservationUseCase cancelReservationUseCase;

    @InjectMocks
    private ReservationService service;

    private final ReservationProcess deUser42 = ReservationProcess.builder().id(1L).externalPatientId("user-42").build();

    @Test
    void devuelveLaReservaASuDueno() {
        when(getReservationByIdUseCase.getReservationById(1L)).thenReturn(Optional.of(deUser42));

        assertThat(service.getReservationForPatient(1L, "user-42")).isSameAs(deUser42);
    }

    @Test
    void unaReservaAjenaSeRespondeComoInexistente() {
        when(getReservationByIdUseCase.getReservationById(1L)).thenReturn(Optional.of(deUser42));

        assertThatThrownBy(() -> service.getReservationForPatient(1L, "user-99"))
                .isInstanceOf(ReservationNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void unaReservaInexistenteLanzaExcepcion() {
        when(getReservationByIdUseCase.getReservationById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReservationForPatient(5L, "user-42"))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void delegaElListadoDelPaciente() {
        when(getReservationsByPatientUseCase.getReservationsByPatient("user-42")).thenReturn(List.of(deUser42));

        assertThat(service.getReservationsByPatient("user-42")).containsExactly(deUser42);
    }

    @Test
    void delegaLaAplicacionDeEventos() {
        ReservationEvent evento = ReservationEvent.builder().eventId("e-1").build();
        when(applyReservationEventUseCase.applyEvent(evento)).thenReturn(true);

        assertThat(service.applyEvent(evento)).isTrue();
    }

    @Test
    void iniciarDelegaEnElCasoDeUso() {
        ReservationRequest request = new ReservationRequest("user-42", 201L, null, null, "Juan", "Perez");
        when(startReservationUseCase.start(request)).thenReturn(deUser42);

        assertThat(service.startReservation(request)).isSameAs(deUser42);
    }

    @Test
    void cancelaSoloLaReservaPropia() {
        when(getReservationByIdUseCase.getReservationById(1L)).thenReturn(Optional.of(deUser42));
        when(cancelReservationUseCase.cancel(deUser42, "motivo")).thenReturn(deUser42);

        assertThat(service.cancelReservation(1L, "user-42", "motivo")).isSameAs(deUser42);
    }

    @Test
    void noCancelaUnaReservaAjena() {
        when(getReservationByIdUseCase.getReservationById(1L)).thenReturn(Optional.of(deUser42));

        assertThatThrownBy(() -> service.cancelReservation(1L, "user-99", null))
                .isInstanceOf(ReservationNotFoundException.class);
        verify(cancelReservationUseCase, never()).cancel(any(), any());
    }
}
