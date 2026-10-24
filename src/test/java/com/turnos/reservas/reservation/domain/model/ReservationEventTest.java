package com.turnos.reservas.reservation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationEventTest {

    private ReservationProcess esperandoTelefono() {
        return ReservationProcess.builder()
                .reservationProcessId("process-a92b47d0")
                .status(ReservationStatus.WAITING_FOR_PHONE)
                .expiresAt(Instant.parse("2026-09-10T14:44:50Z"))
                .build();
    }

    private ReservationEvent evento(ReservationEventType tipo) {
        return ReservationEvent.builder()
                .eventId("confirmation-event-7d10")
                .type(tipo)
                .reservationProcessId("process-a92b47d0")
                .reservationId(8251L)
                .reason("HOLD_EXPIRED")
                .build();
    }

    @Test
    void reconoceLosNombresDelContrato() {
        assertThat(ReservationEventType.fromContractName("AppointmentConfirmed"))
                .contains(ReservationEventType.APPOINTMENT_CONFIRMED);
        assertThat(ReservationEventType.fromContractName("EventoNuevoDesconocido")).isEmpty();
    }

    @Test
    void laConfirmacionConfirmaElProceso() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.APPOINTMENT_CONFIRMED).applyTo(proceso)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(proceso.getReservationId()).isEqualTo(8251L);
    }

    @Test
    void elVencimientoGuardaElMotivo() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.APPOINTMENT_PROCESS_EXPIRED).applyTo(proceso)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.getStatusReason()).isEqualTo("HOLD_EXPIRED");
    }

    @Test
    void unTelefonoRechazadoNoCambiaElEstado() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.ADDITIONAL_INFORMATION_REJECTED).applyTo(proceso)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }
}
