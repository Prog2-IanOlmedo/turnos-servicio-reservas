package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationReconcileTest {

    private static final Instant AHORA = Instant.parse("2026-11-10T14:00:00Z");
    private static final Instant VENCIDO = Instant.parse("2026-11-10T13:50:00Z");
    private static final Instant VIGENTE = Instant.parse("2026-11-10T14:10:00Z");

    private ReservationProcess proceso(ReservationStatus status, Instant vence) {
        return ReservationProcess.builder().reservationProcessId("process-1").externalPatientId("ian")
                .professionalId(202L).date(LocalDate.of(2026, 11, 16)).startTime(LocalTime.of(9, 0))
                .status(status).expiresAt(vence).build();
    }

    private Appointment enLaCatedra(AppointmentStatus status) {
        return new Appointment("process-1", 1662L, status);
    }

    @Test
    void noTocaUnProcesoQueTodaviaNoVencio() {
        ReservationProcess proceso = proceso(ReservationStatus.WAITING_FOR_PHONE, VIGENTE);

        assertThat(proceso.reconcileWith(null, AHORA)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }

    @Test
    void unHoldVencidoQueLaCatedraNoConoceSeVence() {
        ReservationProcess proceso = proceso(ReservationStatus.HELD, VENCIDO);

        assertThat(proceso.reconcileWith(null, AHORA)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.getStatusReason()).isEqualTo("HOLD_EXPIRED");
    }

    @Test
    void siLaCatedraLoConfirmoSeConfirma() {
        ReservationProcess proceso = proceso(ReservationStatus.WAITING_FOR_PHONE, VENCIDO);

        assertThat(proceso.reconcileWith(enLaCatedra(AppointmentStatus.CONFIRMED), AHORA)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(proceso.getReservationId()).isEqualTo(1662L);
    }

    @Test
    void siLaCatedraLoDioPorFallidoSeVence() {
        ReservationProcess proceso = proceso(ReservationStatus.WAITING_FOR_PHONE, VENCIDO);

        assertThat(proceso.reconcileWith(enLaCatedra(AppointmentStatus.FAILED), AHORA)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.getStatusReason()).isEqualTo("FAILED");
    }

    @Test
    void siLaCatedraTodaviaEsperaElTelefonoSeDejaParaDespues() {
        ReservationProcess proceso = proceso(ReservationStatus.WAITING_FOR_PHONE, VENCIDO);

        assertThat(proceso.reconcileWith(enLaCatedra(AppointmentStatus.PHONE_PENDING), AHORA)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }

    @Test
    void unProcesoYaCerradoNoSeReabre() {
        ReservationProcess proceso = proceso(ReservationStatus.EXPIRED, VENCIDO);

        assertThat(proceso.reconcileWith(enLaCatedra(AppointmentStatus.CONFIRMED), AHORA)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void soloHeldYEsperandoTelefonoEstanAbiertos() {
        assertThat(ReservationStatus.HELD.isOpen()).isTrue();
        assertThat(ReservationStatus.WAITING_FOR_PHONE.isOpen()).isTrue();
        assertThat(ReservationStatus.CONFIRMED.isOpen()).isFalse();
        assertThat(ReservationStatus.EXPIRED.isOpen()).isFalse();
    }

    @Test
    void unEstadoDesconocidoDeLaCatedraSeIgnora() {
        assertThat(AppointmentStatus.parse("CONFIRMED")).contains(AppointmentStatus.CONFIRMED);
        assertThat(AppointmentStatus.parse("ESTADO_NUEVO")).isEmpty();
    }
}
