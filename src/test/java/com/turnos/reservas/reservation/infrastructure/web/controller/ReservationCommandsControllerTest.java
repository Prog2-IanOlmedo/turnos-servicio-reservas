package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.application.exception.ReservationNotCancellableException;
import com.turnos.reservas.reservation.application.exception.InvalidPhoneNumberException;
import com.turnos.reservas.reservation.application.exception.PhoneNotRequestedException;
import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.infrastructure.web.mapper.ReservationDtoMapper;
import com.turnos.reservas.shared.web.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReservationCommandsControllerTest {

    private static final String URL = "/api/reservations";
    private static final Principal USUARIO = () -> "user-42";
    private static final String BODY_VALIDO = """
            {"professionalId": 201, "date": "2026-11-09", "startTime": "12:00",
             "patientFirstName": "Juan", "patientLastName": "Perez"}
            """;

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReservationController(reservationService, new ReservationDtoMapper()))
                .setControllerAdvice(new ReservationExceptionHandler(), new GlobalExceptionHandler())
                .build();
    }

    private ReservationProcess procesoEn(ReservationStatus status) {
        return ReservationProcess.builder().id(7L).reservationProcessId("process-1")
                .externalPatientId("user-42").professionalId(201L)
                .date(LocalDate.of(2026, 11, 9)).startTime(LocalTime.of(12, 0))
                .status(status).build();
    }

    @Test
    void iniciaLaReservaConElPacienteDeLaUrl() throws Exception {
        when(reservationService.startReservation(any())).thenReturn(procesoEn(ReservationStatus.WAITING_FOR_PHONE));

        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.status").value("WAITING_FOR_PHONE"));

        ArgumentCaptor<ReservationRequest> pedido = ArgumentCaptor.forClass(ReservationRequest.class);
        verify(reservationService).startReservation(pedido.capture());
        assertThat(pedido.getValue().externalPatientId()).isEqualTo("user-42");
        assertThat(pedido.getValue().startTime()).isEqualTo(LocalTime.of(12, 0));
    }

    @Test
    void rechazaUnPedidoIncompleto() throws Exception {
        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content("{\"professionalId\": 201}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void siElTurnoEstaTomadoDevuelve409ConElCodeDeLaCatedra() throws Exception {
        when(reservationService.startReservation(any()))
                .thenThrow(new CatedraRejectedException(409, "SLOT_ALREADY_HELD"));

        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("SLOT_ALREADY_HELD"));
    }

    @Test
    void unRechazoSinCodeUsaUnCodeGenerico() throws Exception {
        when(reservationService.startReservation(any())).thenThrow(new CatedraRejectedException(400, null));

        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CATEDRA_REJECTED"));
    }

    @Test
    void siFallaNuestraCredencialDevuelve502() throws Exception {
        when(reservationService.startReservation(any())).thenThrow(new CatedraRejectedException(401, null));

        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.code").value("CATEDRA_AUTH_ERROR"));
    }

    @Test
    void siLaCatedraNoRespondeDevuelve503() throws Exception {
        when(reservationService.startReservation(any()))
                .thenThrow(new CatedraUnavailableException("sin respuesta", null));

        mockMvc.perform(post(URL).principal(USUARIO).contentType(MediaType.APPLICATION_JSON).content(BODY_VALIDO))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CATEDRA_UNAVAILABLE"));
    }

    @Test
    void cancelaConMotivo() throws Exception {
        when(reservationService.cancelReservation(7L, "user-42", "Cambio de planes"))
                .thenReturn(procesoEn(ReservationStatus.CANCELLED));

        mockMvc.perform(post(URL + "/7/cancel").principal(USUARIO).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reason\": \"Cambio de planes\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelaSinBody() throws Exception {
        when(reservationService.cancelReservation(any(), any(), isNull()))
                .thenReturn(procesoEn(ReservationStatus.CANCELLED));

        mockMvc.perform(post(URL + "/7/cancel").principal(USUARIO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void noCancelaUnaReservaQueNoEstaConfirmada() throws Exception {
        when(reservationService.cancelReservation(7L, "user-42", null))
                .thenThrow(new ReservationNotCancellableException(7L, ReservationStatus.WAITING_FOR_PHONE));

        mockMvc.perform(post(URL + "/7/cancel").principal(USUARIO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RESERVATION_NOT_CANCELLABLE"));
    }

    @Test
    void elTelefonoSeAceptaConUn202() throws Exception {
        when(reservationService.submitPhone(7L, "user-42", "+54 261 555-1234"))
                .thenReturn(procesoEn(ReservationStatus.WAITING_FOR_PHONE));

        mockMvc.perform(post(URL + "/7/phone").principal(USUARIO).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\": \"+54 261 555-1234\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value("WAITING_FOR_PHONE"))
                .andExpect(jsonPath("$.phoneRequested").value(false));
    }

    @Test
    void unTelefonoInvalidoDevuelve400() throws Exception {
        when(reservationService.submitPhone(7L, "user-42", "abc"))
                .thenThrow(new InvalidPhoneNumberException(new IllegalArgumentException()));

        mockMvc.perform(post(URL + "/7/phone").principal(USUARIO).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\": \"abc\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_PHONE_NUMBER"));
    }

    @Test
    void sinPedidoDeTelefonoDevuelve409() throws Exception {
        when(reservationService.submitPhone(7L, "user-42", "2615551234"))
                .thenThrow(new PhoneNotRequestedException(7L, ReservationStatus.CONFIRMED));

        mockMvc.perform(post(URL + "/7/phone").principal(USUARIO).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\": \"2615551234\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PHONE_NOT_REQUESTED"));
    }

    @Test
    void unTelefonoVacioNoLlegaAlServicio() throws Exception {
        mockMvc.perform(post(URL + "/7/phone").principal(USUARIO).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\": \"\"}"))
                .andExpect(status().isBadRequest());
    }
}
