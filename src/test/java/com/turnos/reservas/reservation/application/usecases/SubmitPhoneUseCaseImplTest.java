package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.application.exception.InvalidPhoneNumberException;
import com.turnos.reservas.reservation.application.exception.PhoneNotRequestedException;
import com.turnos.reservas.reservation.domain.model.PhoneSubmission;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.PhoneSubmissionPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubmitPhoneUseCaseImplTest {

    @Mock
    private PhoneSubmissionPublisher phoneSubmissionPublisher;

    @InjectMocks
    private SubmitPhoneUseCaseImpl useCase;

    private ReservationProcess esperandoTelefono(String requestEventId, Instant vence) {
        return ReservationProcess.builder().id(7L).reservationProcessId("process-1").holdId("hold-1")
                .status(ReservationStatus.WAITING_FOR_PHONE).phoneRequestEventId(requestEventId)
                .expiresAt(vence).build();
    }

    private Instant enDiezMinutos() {
        return Instant.now().plus(Duration.ofMinutes(10));
    }

    @Test
    void publicaLaRespuestaConElRequestEventIdYElTelefonoNormalizado() {
        useCase.submitPhone(esperandoTelefono("request-1", enDiezMinutos()), "+54 9 (261) 555-1234");

        ArgumentCaptor<PhoneSubmission> enviado = ArgumentCaptor.forClass(PhoneSubmission.class);
        verify(phoneSubmissionPublisher).publish(enviado.capture());
        assertThat(enviado.getValue().eventId()).isNotBlank();
        assertThat(enviado.getValue().reservationProcessId()).isEqualTo("process-1");
        assertThat(enviado.getValue().holdId()).isEqualTo("hold-1");
        assertThat(enviado.getValue().requestEventId()).isEqualTo("request-1");
        assertThat(enviado.getValue().phoneNumber().value()).isEqualTo("+5492615551234");
        assertThat(enviado.getValue().occurredAt()).isNotNull();
    }

    @Test
    void cadaEnvioLlevaUnEventIdNuevo() {
        ReservationProcess proceso = esperandoTelefono("request-1", enDiezMinutos());

        useCase.submitPhone(proceso, "2615551234");
        useCase.submitPhone(proceso, "2615559999");

        ArgumentCaptor<PhoneSubmission> enviados = ArgumentCaptor.forClass(PhoneSubmission.class);
        verify(phoneSubmissionPublisher, times(2)).publish(enviados.capture());
        assertThat(enviados.getAllValues().get(0).eventId())
                .isNotEqualTo(enviados.getAllValues().get(1).eventId());
    }

    @Test
    void sinPedidoDeLaCatedraNoSeManda() {
        ReservationProcess proceso = esperandoTelefono(null, enDiezMinutos());

        assertThatThrownBy(() -> useCase.submitPhone(proceso, "2615551234"))
                .isInstanceOf(PhoneNotRequestedException.class);
        verify(phoneSubmissionPublisher, never()).publish(any());
    }

    @Test
    void conElPlazoVencidoNoSeManda() {
        ReservationProcess proceso = esperandoTelefono("request-1", Instant.now().minusSeconds(1));

        assertThatThrownBy(() -> useCase.submitPhone(proceso, "2615551234"))
                .isInstanceOf(PhoneNotRequestedException.class);
        verify(phoneSubmissionPublisher, never()).publish(any());
    }

    @Test
    void unTelefonoInvalidoNoSeManda() {
        ReservationProcess proceso = esperandoTelefono("request-1", enDiezMinutos());

        assertThatThrownBy(() -> useCase.submitPhone(proceso, "abc"))
                .isInstanceOf(InvalidPhoneNumberException.class);
        verify(phoneSubmissionPublisher, never()).publish(any());
    }
}
