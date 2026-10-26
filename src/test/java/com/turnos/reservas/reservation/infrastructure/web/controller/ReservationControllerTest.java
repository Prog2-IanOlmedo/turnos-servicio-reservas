package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.infrastructure.web.mapper.ReservationDtoMapper;
import com.turnos.reservas.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    private final ReservationProcess confirmada = ReservationProcess.builder()
            .id(1L).reservationProcessId("process-a92b47d0").externalPatientId("user-42")
            .professionalId(15L).date(LocalDate.parse("2026-09-14")).startTime(LocalTime.of(9, 30))
            .status(ReservationStatus.CONFIRMED).reservationId(8251L).build();

    @BeforeEach
    void setUp() {
        ReservationController controller = new ReservationController(reservationService, new ReservationDtoMapper());
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new ReservationExceptionHandler(), new GlobalExceptionHandler())
                .build();
    }

    @Test
    void listaLasReservasDelPaciente() throws Exception {
        when(reservationService.getReservationsByPatient("user-42")).thenReturn(List.of(confirmada));

        mockMvc.perform(get("/api/patients/user-42/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$[0].date").value("2026-09-14"))
                .andExpect(jsonPath("$[0].reservationId").value(8251));
    }

    @Test
    void devuelveUnaReservaDelPaciente() throws Exception {
        when(reservationService.getReservationForPatient(1L, "user-42")).thenReturn(confirmada);

        mockMvc.perform(get("/api/patients/user-42/reservations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationProcessId").value("process-a92b47d0"))
                .andExpect(jsonPath("$.holdId").doesNotExist());
    }

    @Test
    void unaReservaAjenaRespondeNotFoundEnProblemJson() throws Exception {
        when(reservationService.getReservationForPatient(1L, "user-99"))
                .thenThrow(new ReservationNotFoundException(1L));

        mockMvc.perform(get("/api/patients/user-99/reservations/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.code").value("RESERVATION_NOT_FOUND"));
    }

    @Test
    void unIdNoNumericoRespondeBadRequest() throws Exception {
        mockMvc.perform(get("/api/patients/user-42/reservations/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void unErrorInesperadoNoMuestraDetallesInternos() throws Exception {
        when(reservationService.getReservationsByPatient("user-42"))
                .thenThrow(new IllegalStateException("detalle interno"));

        mockMvc.perform(get("/api/patients/user-42/reservations"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.detail").value("Ocurrió un error inesperado."));
    }
}
