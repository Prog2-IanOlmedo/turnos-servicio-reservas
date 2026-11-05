package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartReservationUseCaseImplTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 10);
    private static final LocalTime HORA = LocalTime.of(9, 30);
    private static final Instant VENCE_HOLD = Instant.parse("2026-11-10T12:10:00Z");
    private static final Instant VENCE_TELEFONO = Instant.parse("2026-11-10T12:20:00Z");

    private final ReservationRequest request =
            new ReservationRequest("user-42", 201L, FECHA, HORA, "Juan", "Perez");
    private final Hold hold = new Hold("hold-1", "process-1", VENCE_HOLD);

    @Mock
    private AppointmentGateway appointmentGateway;

    @Mock
    private ReservationProcessRepository reservationProcessRepository;

    @InjectMocks
    private StartReservationUseCaseImpl useCase;

    @BeforeEach
    void guardarDevuelveLoMismo() {
        lenient().when(reservationProcessRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void tomaElTurnoLoConfirmaYQuedaEsperandoElTelefono() {
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42")).thenReturn(hold);
        when(appointmentGateway.confirmHold("hold-1", "user-42", "Juan", "Perez")).thenReturn(VENCE_TELEFONO);

        ReservationProcess process = useCase.start(request);

        assertThat(process.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
        assertThat(process.getReservationProcessId()).isEqualTo("process-1");
        assertThat(process.getHoldId()).isEqualTo("hold-1");
        assertThat(process.getExternalPatientId()).isEqualTo("user-42");
        assertThat(process.getProfessionalId()).isEqualTo(201L);
        assertThat(process.getDate()).isEqualTo(FECHA);
        assertThat(process.getStartTime()).isEqualTo(HORA);
        assertThat(process.getExpiresAt()).isEqualTo(VENCE_TELEFONO);
        verify(reservationProcessRepository, times(2)).save(any());
    }

    @Test
    void siElTurnoEstaTomadoNoGuardaNada() {
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42"))
                .thenThrow(new CatedraRejectedException(409, "SLOT_ALREADY_HELD"));

        assertThatThrownBy(() -> useCase.start(request)).isInstanceOf(CatedraRejectedException.class);
        verify(reservationProcessRepository, never()).save(any());
    }

    @Test
    void siElHoldVencioAlConfirmarCierraElProceso() {
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42")).thenReturn(hold);
        when(appointmentGateway.confirmHold("hold-1", "user-42", "Juan", "Perez"))
                .thenThrow(new CatedraRejectedException(404, "HOLD_EXPIRED"));

        assertThatThrownBy(() -> useCase.start(request)).isInstanceOf(CatedraRejectedException.class);

        ArgumentCaptor<ReservationProcess> guardado = ArgumentCaptor.forClass(ReservationProcess.class);
        verify(reservationProcessRepository, times(2)).save(guardado.capture());
        assertThat(guardado.getValue().getStatus()).isEqualTo(ReservationStatus.EXPIRED);
        assertThat(guardado.getValue().getStatusReason()).isEqualTo("HOLD_EXPIRED");
    }

    @Test
    void siLaConfirmacionSeCortaElProcesoQuedaGuardadoEnHeld() {
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42")).thenReturn(hold);
        when(appointmentGateway.confirmHold("hold-1", "user-42", "Juan", "Perez"))
                .thenThrow(new CatedraUnavailableException("sin respuesta", null));

        assertThatThrownBy(() -> useCase.start(request)).isInstanceOf(CatedraUnavailableException.class);

        ArgumentCaptor<ReservationProcess> guardado = ArgumentCaptor.forClass(ReservationProcess.class);
        verify(reservationProcessRepository).save(guardado.capture());
        assertThat(guardado.getValue().getStatus()).isEqualTo(ReservationStatus.HELD);
        assertThat(guardado.getValue().getExpiresAt()).isEqualTo(VENCE_HOLD);
    }

    @Test
    void otroRechazoAlConfirmarNoCambiaElEstado() {
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42")).thenReturn(hold);
        when(appointmentGateway.confirmHold("hold-1", "user-42", "Juan", "Perez"))
                .thenThrow(new CatedraRejectedException(400, "VALIDATION_ERROR"));

        assertThatThrownBy(() -> useCase.start(request)).isInstanceOf(CatedraRejectedException.class);
        verify(reservationProcessRepository, times(1)).save(any());
    }

    @Test
    void siKafkaYaRegistroElPedidoDeTelefonoNoLoPisa() {
        ReservationProcess yaEsperando = ReservationProcess.builder().id(7L).reservationProcessId("process-1")
                .status(ReservationStatus.WAITING_FOR_PHONE).phoneRequestEventId("request-1")
                .expiresAt(VENCE_TELEFONO).build();
        when(appointmentGateway.createHold(201L, FECHA, HORA, "user-42")).thenReturn(hold);
        when(appointmentGateway.confirmHold("hold-1", "user-42", "Juan", "Perez")).thenReturn(VENCE_TELEFONO);
        when(reservationProcessRepository.findById(any())).thenReturn(Optional.of(yaEsperando));

        ReservationProcess process = useCase.start(request);

        assertThat(process.getPhoneRequestEventId()).isEqualTo("request-1");
        assertThat(process.getStatus()).isEqualTo(ReservationStatus.WAITING_FOR_PHONE);
    }
}
