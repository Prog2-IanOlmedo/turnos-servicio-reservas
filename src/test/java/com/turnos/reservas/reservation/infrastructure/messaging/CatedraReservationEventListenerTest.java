package com.turnos.reservas.reservation.infrastructure.messaging;

import java.time.Instant;

import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationEventType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatedraReservationEventListenerTest {

    @Mock
    private ReservationService reservationService;

    private CatedraReservationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new CatedraReservationEventListener(reservationService, new CatedraEventMapper(),
                JsonMapper.builder().build());
    }

    private ReservationEvent eventoAplicado() {
        ArgumentCaptor<ReservationEvent> evento = ArgumentCaptor.forClass(ReservationEvent.class);
        verify(reservationService).applyEvent(evento.capture());
        return evento.getValue();
    }

    @Test
    void aplicaUnaConfirmacionIgnorandoCamposQueNoConoce() {
        when(reservationService.applyEvent(any())).thenReturn(true);

        listener.onMessage("""
                {"eventId": "confirmation-event-7d10", "eventType": "AppointmentConfirmed",
                 "groupId": "proyecto-ian-olmedo", "reservationProcessId": "process-1",
                 "reservationId": 8251, "status": "CONFIRMED", "campoNuevo": true, "schemaVersion": 1}
                """);

        ReservationEvent evento = eventoAplicado();
        assertThat(evento.getType()).isEqualTo(ReservationEventType.APPOINTMENT_CONFIRMED);
        assertThat(evento.getReservationId()).isEqualTo(8251L);
        assertThat(evento.getReservationProcessId()).isEqualTo("process-1");
    }

    @Test
    void registraElPedidoDeTelefonoConSuPlazo() {
        listener.onMessage("""
                {"eventId": "request-event-8a31", "eventType": "AdditionalInformationRequested",
                 "reservationProcessId": "process-1", "holdId": "hold-1", "requiredField": "PHONE_NUMBER",
                 "expiresAt": "2026-11-09T15:20:00Z", "schemaVersion": 1}
                """);

        ReservationEvent evento = eventoAplicado();
        assertThat(evento.getType()).isEqualTo(ReservationEventType.ADDITIONAL_INFORMATION_REQUESTED);
        assertThat(evento.getEventId()).isEqualTo("request-event-8a31");
        assertThat(evento.getExpiresAt()).isEqualTo(Instant.parse("2026-11-09T15:20:00Z"));
    }

    @Test
    void ignoraUnTipoDeEventoDesconocido() {
        listener.onMessage("""
                {"eventId": "x-1", "eventType": "EventoQueTodaviaNoExiste", "reservationProcessId": "process-1"}
                """);

        verify(reservationService, never()).applyEvent(any());
    }

    @Test
    void ignoraUnEventoSinIds() {
        listener.onMessage("{\"eventType\": \"AppointmentConfirmed\"}");

        verify(reservationService, never()).applyEvent(any());
    }

    @Test
    void ignoraUnMensajeIlegible() {
        listener.onMessage("esto no es json");

        verify(reservationService, never()).applyEvent(any());
    }
}
