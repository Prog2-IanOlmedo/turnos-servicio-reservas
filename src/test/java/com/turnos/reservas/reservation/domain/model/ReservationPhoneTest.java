package com.turnos.reservas.reservation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationPhoneTest {

    private static final Instant PLAZO = Instant.parse("2026-11-09T15:20:00Z");

    private ReservationProcess enEstado(ReservationStatus status) {
        return ReservationProcess.builder().reservationProcessId("process-1").status(status).build();
    }

    @Test
    void registraElPedidoYElNuevoPlazo() {
        ReservationProcess proceso = enEstado(ReservationStatus.WAITING_FOR_PHONE);

        assertThat(proceso.registerPhoneRequest("request-1", PLAZO)).isTrue();
        assertThat(proceso.getPhoneRequestEventId()).isEqualTo("request-1");
        assertThat(proceso.getExpiresAt()).isEqualTo(PLAZO);
        assertThat(proceso.canSubmitPhone()).isTrue();
    }

    @Test
    void unPedidoQueLlegaAntesDeLaConfirmacionRestIgualSeRegistra() {
        ReservationProcess proceso = enEstado(ReservationStatus.HELD);

        assertThat(proceso.registerPhoneRequest("request-1", PLAZO)).isTrue();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(proceso.canSubmitPhone()).isTrue();
    }

    @Test
    void elMismoPedidoDosVecesNoCambiaNada() {
        ReservationProcess proceso = enEstado(ReservationStatus.WAITING_FOR_PHONE);
        proceso.registerPhoneRequest("request-1", PLAZO);

        assertThat(proceso.registerPhoneRequest("request-1", PLAZO)).isFalse();
    }

    @Test
    void unPedidoTardioNoReabreUnEstadoFinal() {
        ReservationProcess proceso = enEstado(ReservationStatus.EXPIRED);

        assertThat(proceso.registerPhoneRequest("request-1", PLAZO)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.canSubmitPhone()).isFalse();
    }

    @Test
    void unPedidoSinEventIdSeIgnora() {
        ReservationProcess proceso = enEstado(ReservationStatus.HELD);

        assertThat(proceso.registerPhoneRequest(null, PLAZO)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.HELD);
    }

    @Test
    void unPedidoSinPlazoConservaElAnterior() {
        ReservationProcess proceso = ReservationProcess.builder()
                .status(ReservationStatus.WAITING_FOR_PHONE).expiresAt(PLAZO).build();

        assertThat(proceso.registerPhoneRequest("request-1", null)).isTrue();
        assertThat(proceso.getExpiresAt()).isEqualTo(PLAZO);
    }

    @Test
    void elRechazoFueraDeTiempoNoHaceNada() {
        ReservationProcess proceso = enEstado(ReservationStatus.CONFIRMED);

        assertThat(proceso.rejectPhone("INVALID_PHONE_NUMBER", PLAZO)).isFalse();
        assertThat(proceso.getStatusReason()).isNull();
    }

    @Test
    void elRechazoSinPlazoConservaElAnterior() {
        ReservationProcess proceso = ReservationProcess.builder()
                .status(ReservationStatus.WAITING_FOR_PHONE).expiresAt(PLAZO).build();

        assertThat(proceso.rejectPhone("INVALID_PHONE_NUMBER", null)).isTrue();
        assertThat(proceso.getExpiresAt()).isEqualTo(PLAZO);
    }

    @Test
    void sinPedidoTodaviaNoSePuedeMandarElTelefono() {
        assertThat(enEstado(ReservationStatus.WAITING_FOR_PHONE).canSubmitPhone()).isFalse();
    }

    @Test
    void normalizaElTelefonoComoLaCatedra() {
        assertThat(PhoneNumber.parse("+54 9 (261) 555-1234").value()).isEqualTo("+5492615551234");
        assertThat(PhoneNumber.parse("261.555.12").value()).isEqualTo("26155512");
    }

    @Test
    void rechazaTelefonosInvalidos() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> PhoneNumber.parse("abc1234567"))
                .isInstanceOf(IllegalArgumentException.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> PhoneNumber.parse("123456"))
                .isInstanceOf(IllegalArgumentException.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> PhoneNumber.parse("1234567890123456"))
                .isInstanceOf(IllegalArgumentException.class);
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> PhoneNumber.parse(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
