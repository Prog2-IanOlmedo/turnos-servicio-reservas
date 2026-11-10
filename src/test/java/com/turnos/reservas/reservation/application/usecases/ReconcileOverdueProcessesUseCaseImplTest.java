package com.turnos.reservas.reservation.application.usecases;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.AppointmentStatus;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconcileOverdueProcessesUseCaseImplTest {

    @Mock
    private ReservationProcessRepository processRepository;

    @Mock
    private AppointmentGateway appointmentGateway;

    @InjectMocks
    private ReconcileOverdueProcessesUseCaseImpl useCase;

    private ReservationProcess proceso(String id, ReservationStatus status, Instant vence) {
        return ReservationProcess.builder().reservationProcessId(id).externalPatientId("ian")
                .professionalId(202L).date(LocalDate.of(2026, 11, 16)).startTime(LocalTime.of(9, 0))
                .status(status).expiresAt(vence).build();
    }

    private Instant haceUnaHora() {
        return Instant.now().minusSeconds(3600);
    }

    @Test
    void consultaUnaVezPorPacienteYCierraLosVencidos() {
        ReservationProcess confirmado = proceso("process-1", ReservationStatus.WAITING_FOR_PHONE, haceUnaHora());
        ReservationProcess sinReserva = proceso("process-2", ReservationStatus.HELD, haceUnaHora());
        ReservationProcess vigente = proceso("process-3", ReservationStatus.WAITING_FOR_PHONE,
                Instant.now().plusSeconds(600));
        when(processRepository.findOpenProcesses()).thenReturn(List.of(confirmado, sinReserva, vigente));
        when(appointmentGateway.findAppointments("ian"))
                .thenReturn(List.of(new Appointment("process-1", 1662L, AppointmentStatus.CONFIRMED)));

        assertThat(useCase.reconcile()).isEqualTo(2);

        verify(appointmentGateway, times(1)).findAppointments("ian");
        verify(processRepository).save(confirmado);
        verify(processRepository).save(sinReserva);
        verify(processRepository, never()).save(vigente);
        assertThat(confirmado.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(sinReserva.getStatus()).isEqualTo(ReservationStatus.EXPIRED);
    }

    @Test
    void sinProcesosVencidosNoLlamaALaCatedra() {
        when(processRepository.findOpenProcesses()).thenReturn(List.of(
                proceso("process-3", ReservationStatus.WAITING_FOR_PHONE, Instant.now().plusSeconds(600))));

        assertThat(useCase.reconcile()).isZero();
        verify(appointmentGateway, never()).findAppointments(anyString());
    }

    @Test
    void reservaDuplicadaEnLaRespuestaNoRompe() {
        ReservationProcess proceso = proceso("process-1", ReservationStatus.WAITING_FOR_PHONE, haceUnaHora());
        when(processRepository.findOpenProcesses()).thenReturn(List.of(proceso));
        when(appointmentGateway.findAppointments("ian")).thenReturn(List.of(
                new Appointment("process-1", 1662L, AppointmentStatus.CONFIRMED),
                new Appointment("process-1", 1662L, AppointmentStatus.CONFIRMED)));

        assertThat(useCase.reconcile()).isEqualTo(1);
    }

    @Test
    void siLaCatedraNoRespondeNoSeCierraNada() {
        when(processRepository.findOpenProcesses()).thenReturn(List.of(
                proceso("process-1", ReservationStatus.HELD, haceUnaHora())));
        when(appointmentGateway.findAppointments("ian"))
                .thenThrow(new CatedraUnavailableException("sin respuesta", null));

        assertThatThrownBy(() -> useCase.reconcile()).isInstanceOf(CatedraUnavailableException.class);
        verify(processRepository, never()).save(any());
    }
}
