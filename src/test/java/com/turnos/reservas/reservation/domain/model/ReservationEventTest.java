package com.turnos.reservas.reservation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationEventTest {

    private static final Instant NUEVO_PLAZO = Instant.parse("2026-09-10T14:50:00Z");

    private ReservationProcess esperandoTelefono() {
        return ReservationProcess.builder()
                .reservationProcessId("process-a92b47d0")
                .status(ReservationStatus.WAITING_FOR_PHONE)
                .expiresAt(Instant.parse("2026-09-10T14:44:50Z"))
                .build();
    }

    private ReservationEvent evento(ReservationEventType tipo, String reason) {
        return ReservationEvent.builder()
                .eventId("event-7d10")
                .type(tipo)
                .reservationProcessId("process-a92b47d0")
                .reservationId(8251L)
                .reason(reason)
                .expiresAt(NUEVO_PLAZO)
                .build();
    }

    @Test
    void reconoceLosNombresDelContrato() {
        assertThat(ReservationEventType.fromContractName("AppointmentConfirmed"))
                .contains(ReservationEventType.APPOINTMENT_CONFIRMED);
        assertThat(ReservationEventType.fromContractName("EventoNuevoDesconocido")).isEmpty();
    }

    @Test
    void elPedidoDeTelefonoGuardaSuEventIdYElPlazo() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.ADDITIONAL_INFORMATION_REQUESTED, null).applyTo(proceso)).isTrue();
        assertThat(proceso.getPhoneRequestEventId()).isEqualTo("event-7d10");
        assertThat(proceso.getExpiresAt()).isEqualTo(NUEVO_PLAZO);
    }

    @Test
    void laConfirmacionConfirmaElProceso() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.APPOINTMENT_CONFIRMED, null).applyTo(proceso)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(proceso.getReservationId()).isEqualTo(8251L);
    }

    @Test
    void elVencimientoGuardaElMotivo() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.APPOINTMENT_PROCESS_EXPIRED, "HOLD_EXPIRED").applyTo(proceso)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.getStatusReason()).isEqualTo("HOLD_EXPIRED");
    }

    @Test
    void unTelefonoRechazadoDejaElMotivoYSigueEsperando() {
        ReservationProcess proceso = esperandoTelefono();

        assertThat(evento(ReservationEventType.ADDITIONAL_INFORMATION_REJECTED, "INVALID_PHONE_NUMBER")
                .applyTo(proceso)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(proceso.getStatusReason()).isEqualTo("INVALID_PHONE_NUMBER");
        assertThat(proceso.getExpiresAt()).isEqualTo(NUEVO_PLAZO);
    }

    @Test
    void elProcesoInvalidoYLaCancelacionTambienSeAplican() {
        ReservationProcess invalido = esperandoTelefono();
        assertThat(evento(ReservationEventType.APPOINTMENT_PROCESS_INVALID, "REQUEST_EVENT_MISMATCH")
                .applyTo(invalido)).isTrue();
        assertThat(invalido.getStatus()).isEqualTo(ReservationStatus.INVALID);

        ReservationProcess confirmado = esperandoTelefono();
        confirmado.confirm(8251L);
        assertThat(evento(ReservationEventType.APPOINTMENT_CANCELLED, null).applyTo(confirmado)).isTrue();
        assertThat(confirmado.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }
}
