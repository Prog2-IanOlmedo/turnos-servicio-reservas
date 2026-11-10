package com.turnos.reservas.reservation.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.AppointmentStatus;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.Occupancy;
import com.turnos.reservas.reservation.domain.model.OccupancyStatus;
import com.turnos.reservas.reservation.infrastructure.client.mapper.CatedraAppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class CatedraAppointmentClientTest {

    private static final String BASE = "http://catedra.test";    private static final LocalDate FECHA = LocalDate.of(2026, 11, 10);
    private static final LocalTime HORA = LocalTime.of(9, 30);

    private MockRestServiceServer server;
    private CatedraAppointmentClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl(BASE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer jwt-de-prueba");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new CatedraAppointmentClient(builder.build(), new CatedraAppointmentMapper());
    }

    @Test
    void traeLasOcupacionesDelProfesional() {
        server.expect(requestTo(BASE + "/api/appointment-occupancies?professionalId=201&from=2026-11-10&to=2026-11-10"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer jwt-de-prueba"))
                .andRespond(withSuccess("""
                        {"professionalId": 201, "from": "2026-11-10", "to": "2026-11-10",
                         "occupancies": [
                           {"date": "2026-11-10", "startTime": "09:00:00", "status": "CONFIRMED", "expiresAt": null},
                           {"date": "2026-11-10", "startTime": "09:30", "status": "HELD", "expiresAt": "2026-11-10T12:10:00Z"}
                         ]}
                        """, MediaType.APPLICATION_JSON));

        List<Occupancy> ocupaciones = client.findOccupancies(201L, FECHA, FECHA);

        server.verify();
        assertThat(ocupaciones).hasSize(2);
        assertThat(ocupaciones.get(0).status()).isEqualTo(OccupancyStatus.CONFIRMED);
        assertThat(ocupaciones.get(0).expiresAt()).isNull();
        assertThat(ocupaciones.get(1).startTime()).isEqualTo(HORA);
        assertThat(ocupaciones.get(1).status()).isEqualTo(OccupancyStatus.HELD);
        assertThat(ocupaciones.get(1).expiresAt()).isEqualTo(Instant.parse("2026-11-10T12:10:00Z"));
    }

    @Test
    void sinOcupacionesDevuelveListaVacia() {
        server.expect(requestTo(BASE + "/api/appointment-occupancies?professionalId=201&from=2026-11-10&to=2026-11-10"))
                .andRespond(withSuccess("{\"professionalId\": 201, \"occupancies\": []}", MediaType.APPLICATION_JSON));

        assertThat(client.findOccupancies(201L, FECHA, FECHA)).isEmpty();
    }

    @Test
    void tomaElTurnoMandandoFechaYHoraComoPideElContrato() {
        server.expect(requestTo(BASE + "/api/appointment-holds"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.professionalId").value(201))
                .andExpect(jsonPath("$.date").value("2026-11-10"))
                .andExpect(jsonPath("$.startTime").value("09:30"))
                .andExpect(jsonPath("$.externalPatientId").value("user-42"))
                .andRespond(withStatus(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"holdId": "hold-1", "reservationProcessId": "process-1",
                         "status": "HELD", "expiresAt": "2026-11-10T12:10:00Z"}
                        """));

        Hold hold = client.createHold(201L, FECHA, HORA, "user-42");

        server.verify();
        assertThat(hold.holdId()).isEqualTo("hold-1");
        assertThat(hold.reservationProcessId()).isEqualTo("process-1");
        assertThat(hold.expiresAt()).isEqualTo(Instant.parse("2026-11-10T12:10:00Z"));
    }

    @Test
    void siElTurnoYaEstaTomadoDevuelveElCodeDeLaCatedra() {
        server.expect(requestTo(BASE + "/api/appointment-holds"))
                .andRespond(withStatus(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_PROBLEM_JSON).body("""
                        {"title": "Conflict", "status": 409, "detail": "Slot already held",
                         "code": "SLOT_ALREADY_HELD"}
                        """));

        assertThatThrownBy(() -> client.createHold(201L, FECHA, HORA, "user-42"))
                .isInstanceOfSatisfying(CatedraRejectedException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(409);
                    assertThat(e.getCode()).isEqualTo("SLOT_ALREADY_HELD");
                    assertThat(e.hasCode("SLOT_ALREADY_HELD")).isTrue();
                });
    }

    @Test
    void confirmaElHoldYDevuelveElNuevoPlazo() {
        server.expect(requestTo(BASE + "/api/appointment-holds/hold-1/confirm"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.externalPatientId").value("user-42"))
                .andExpect(jsonPath("$.patientFirstName").value("Juan"))
                .andExpect(jsonPath("$.patientLastName").value("Perez"))
                .andRespond(withStatus(HttpStatus.ACCEPTED).contentType(MediaType.APPLICATION_JSON).body("""
                        {"reservationProcessId": "process-1", "status": "WAITING_FOR_PHONE",
                         "expiresAt": "2026-11-10T12:20:00Z"}
                        """));

        Instant plazo = client.confirmHold("hold-1", "user-42", "Juan", "Perez");

        server.verify();
        assertThat(plazo).isEqualTo(Instant.parse("2026-11-10T12:20:00Z"));
    }

    @Test
    void cancelaMandandoElMotivo() {
        server.expect(requestTo(BASE + "/api/appointments/process-1/cancel"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.reason").value("Cambio de planes"))
                .andRespond(withSuccess("""
                        {"reservationId": 8251, "reservationProcessId": "process-1", "status": "CANCELLED"}
                        """, MediaType.APPLICATION_JSON));

        client.cancel("process-1", "Cambio de planes");

        server.verify();
    }

    @Test
    void un401SinCodeIgualSeInformaComoRechazo() {
        server.expect(requestTo(BASE + "/api/appointment-holds/hold-1/confirm"))
                .andRespond(withUnauthorizedRequest());

        assertThatThrownBy(() -> client.confirmHold("hold-1", "user-42", "Juan", "Perez"))
                .isInstanceOfSatisfying(CatedraRejectedException.class, e -> {
                    assertThat(e.getStatus()).isEqualTo(401);
                    assertThat(e.getCode()).isNull();
                });
    }

    @Test
    void siLaCatedraFallaSeInformaComoNoDisponible() {
        server.expect(requestTo(BASE + "/api/appointments/process-1/cancel"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> client.cancel("process-1", null))
                .isInstanceOf(CatedraUnavailableException.class);
    }

    @Test
    void unHoldSinCuerpoSeInformaComoNoDisponible() {
        server.expect(requestTo(BASE + "/api/appointment-holds"))
                .andRespond(withStatus(HttpStatus.CREATED));

        assertThatThrownBy(() -> client.createHold(201L, FECHA, HORA, "user-42"))
                .isInstanceOf(CatedraUnavailableException.class)
                .hasMessageContaining("sin cuerpo");
    }

    @Test
    void traeLasReservasDelPacienteSinFiltroDeFechas() {
        server.expect(requestTo(BASE + "/api/appointments?externalPatientId=ian-demo&size=100"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""
                        [{"reservationId": 1662, "reservationProcessId": "process-c2d5", "externalPatientId": "ian-demo",
                          "appointmentDate": "2026-11-09", "status": "CONFIRMED"},
                         {"reservationId": null, "reservationProcessId": "process-0d36", "status": "FAILED"},
                         {"reservationId": 9, "reservationProcessId": "process-x", "status": "ESTADO_NUEVO"}]
                        """, MediaType.APPLICATION_JSON));

        List<Appointment> reservas = client.findAppointments("ian-demo");

        server.verify();
        assertThat(reservas).containsExactly(
                new Appointment("process-c2d5", 1662L, AppointmentStatus.CONFIRMED),
                new Appointment("process-0d36", null, AppointmentStatus.FAILED));
    }

    @Test
    void sinReservasDevuelveListaVacia() {
        server.expect(requestTo(BASE + "/api/appointments?externalPatientId=nadie&size=100"))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        assertThat(client.findAppointments("nadie")).isEmpty();
    }
}
