package com.turnos.reservas.reservation.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationProcessTest {

    private static final Instant VENCE = Instant.parse("2026-09-10T14:44:50Z");

    private ReservationProcess enEstado(ReservationStatus estado) {
        return ReservationProcess.builder()
                .reservationProcessId("process-a92b47d0")
                .status(estado)
                .expiresAt(VENCE)
                .build();
    }

    @Test
    void recorreElFlujoCompletoHastaCancelar() {
        ReservationProcess proceso = enEstado(ReservationStatus.HELD);

        assertThat(proceso.markWaitingForPhone(VENCE)).isTrue();
        assertThat(proceso.confirm(8251L)).isTrue();
        assertThat(proceso.cancel("Cambio solicitado por el usuario")).isTrue();

        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(proceso.getReservationId()).isEqualTo(8251L);
        assertThat(proceso.getStatusReason()).isEqualTo("Cambio solicitado por el usuario");
    }

    @Test
    void unEventoDuplicadoNoCambiaNada() {
        ReservationProcess proceso = enEstado(ReservationStatus.WAITING_FOR_PHONE);
        proceso.confirm(8251L);

        assertThat(proceso.confirm(9999L)).isFalse();
        assertThat(proceso.getReservationId()).isEqualTo(8251L);
    }

    @Test
    void unEventoTardioNoReabreUnEstadoFinal() {
        ReservationProcess proceso = enEstado(ReservationStatus.WAITING_FOR_PHONE);
        proceso.expire("HOLD_EXPIRED");

        assertThat(proceso.confirm(8251L)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(proceso.getStatusReason()).isEqualTo("HOLD_EXPIRED");
    }

    @Test
    void unaReservaConfirmadaNoRetrocede() {
        ReservationProcess proceso = enEstado(ReservationStatus.CONFIRMED);

        assertThat(proceso.expire("HOLD_EXPIRED")).isFalse();
        assertThat(proceso.markWaitingForPhone(VENCE)).isFalse();
        assertThat(proceso.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void soloSeCancelaUnaReservaConfirmada() {
        assertThat(enEstado(ReservationStatus.HELD).cancel("motivo")).isFalse();
        assertThat(enEstado(ReservationStatus.WAITING_FOR_PHONE).cancel("motivo")).isFalse();
    }

    @Test
    void unProcesoAbiertoVenceAlLlegarSuPlazo() {
        ReservationProcess proceso = enEstado(ReservationStatus.WAITING_FOR_PHONE);

        assertThat(proceso.isOverdueAt(VENCE.minusSeconds(1))).isFalse();
        assertThat(proceso.isOverdueAt(VENCE)).isTrue();
        assertThat(enEstado(ReservationStatus.CONFIRMED).isOverdueAt(VENCE)).isFalse();
    }

    @Test
    void losEstadosFinalesNoAceptanTransiciones() {
        for (ReservationStatus estado : ReservationStatus.values()) {
            if (estado.isFinal()) {
                for (ReservationStatus destino : ReservationStatus.values()) {
                    assertThat(estado.canMoveTo(destino)).isFalse();
                }
            }
        }
    }

    @Test
    void soloPerteneceASuPaciente() {
        ReservationProcess proceso = ReservationProcess.builder().externalPatientId("user-42").build();

        assertThat(proceso.belongsTo("user-42")).isTrue();
        assertThat(proceso.belongsTo("user-99")).isFalse();
        assertThat(proceso.belongsTo(null)).isFalse();
    }
}
