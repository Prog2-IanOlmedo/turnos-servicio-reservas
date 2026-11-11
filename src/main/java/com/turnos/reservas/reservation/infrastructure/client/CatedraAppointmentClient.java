package com.turnos.reservas.reservation.infrastructure.client;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.Occupancy;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraAppointmentDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraCancelRequestDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraConfirmRequestDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraConfirmResponseDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraHoldRequestDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraHoldResponseDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraOccupanciesDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraProblemDto;
import com.turnos.reservas.reservation.infrastructure.client.mapper.CatedraAppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// Adaptador del puerto AppointmentGateway contra la API de la cátedra.
// 4xx -> CatedraRejectedException con el code del contrato.
// 5xx, timeout o respuesta vacía -> CatedraUnavailableException.
@Component
@RequiredArgsConstructor
public class CatedraAppointmentClient implements AppointmentGateway {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
    // La cátedra pagina de a 20 por defecto; un paciente no debería tener más de 100 abiertas.
    private static final int APPOINTMENTS_PAGE_SIZE = 100;

    private final RestClient catedraRestClient;
    private final CatedraAppointmentMapper mapper;

    @Override
    public List<Occupancy> findOccupancies(Long professionalId, LocalDate from, LocalDate to) {
        CatedraOccupanciesDto dto = call(() -> catedraRestClient.get()
                .uri(uri -> uri.path("/api/appointment-occupancies")
                        .queryParam("professionalId", professionalId)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CatedraOccupanciesDto.class));
        return mapper.toOccupancies(dto);
    }

    @Override
    public Hold createHold(Long professionalId, LocalDate date, LocalTime startTime, String externalPatientId) {
        CatedraHoldRequestDto request = new CatedraHoldRequestDto(
                professionalId, date.toString(), startTime.format(HORA), externalPatientId);
        CatedraHoldResponseDto dto = call(() -> catedraRestClient.post()
                .uri("/api/appointment-holds")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CatedraHoldResponseDto.class));
        return mapper.toHold(requireBody(dto));
    }

    @Override
    public Instant confirmHold(String holdId, String externalPatientId,
                               String patientFirstName, String patientLastName) {
        CatedraConfirmRequestDto request = new CatedraConfirmRequestDto(
                externalPatientId, patientFirstName, patientLastName);
        CatedraConfirmResponseDto dto = call(() -> catedraRestClient.post()
                .uri("/api/appointment-holds/{holdId}/confirm", holdId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(CatedraConfirmResponseDto.class));
        return requireBody(dto).expiresAt();
    }

    @Override
    public void cancel(String reservationProcessId, String reason) {
        call(() -> catedraRestClient.post()
                .uri("/api/appointments/{reservationProcessId}/cancel", reservationProcessId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CatedraCancelRequestDto(reason))
                .retrieve()
                .toBodilessEntity());
    }

    // Sin filtro de fechas: con from/to la cátedra hoy responde 500 (reportado en Slack).
    @Override
    public List<Appointment> findAppointments(String externalPatientId) {
        CatedraAppointmentDto[] dtos = call(() -> catedraRestClient.get()
                .uri(uri -> uri.path("/api/appointments")
                        .queryParam("externalPatientId", externalPatientId)
                        .queryParam("size", APPOINTMENTS_PAGE_SIZE)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CatedraAppointmentDto[].class));
        return mapper.toAppointments(dtos);
    }

    private <T> T call(Supplier<T> request) {
        try {
            return request.get();
        } catch (HttpClientErrorException e) {
            throw new CatedraRejectedException(e.getStatusCode().value(), codeOf(e));
        } catch (RestClientException e) {
            throw new CatedraUnavailableException("La cátedra no respondió", e);
        }
    }

    // Los 401/403 de Spring Security pueden venir sin body ni code.
    private String codeOf(HttpClientErrorException e) {
        try {
            CatedraProblemDto problem = e.getResponseBodyAs(CatedraProblemDto.class);
            return problem != null ? problem.code() : null;
        } catch (RuntimeException sinCuerpoLegible) {
            return null;
        }
    }

    private <T> T requireBody(T body) {
        if (body == null) {
            throw new CatedraUnavailableException("La cátedra respondió sin cuerpo", null);
        }
        return body;
    }
}
