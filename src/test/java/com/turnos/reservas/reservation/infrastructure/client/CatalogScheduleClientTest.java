package com.turnos.reservas.reservation.infrastructure.client;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.application.exception.CatalogUnavailableException;
import com.turnos.reservas.reservation.application.exception.ProfessionalNotFoundException;
import com.turnos.reservas.reservation.domain.model.WeeklySlots;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CatalogScheduleClientTest {

    private static final String BASE = "http://catalogo";

    private MockRestServiceServer server;
    private CatalogScheduleClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE);
        server = MockRestServiceServer.bindTo(builder).build();
        client = new CatalogScheduleClient(builder.build());
    }

    @Test
    void traeLaAgendaSemanal() {
        server.expect(requestTo(BASE + "/api/professionals/202/schedules"))
                .andRespond(withSuccess("""
                        [{"id": 303, "professionalId": 202, "dayOfWeek": "TUESDAY", "startTime": "11:30:00",
                          "endTime": "14:30:00", "slotDurationMinutes": 30, "slots": ["11:30:00", "12:00:00"]}]
                        """, MediaType.APPLICATION_JSON));

        assertThat(client.findWeeklySlots(202L)).containsExactly(
                new WeeklySlots(DayOfWeek.TUESDAY, List.of(LocalTime.of(11, 30), LocalTime.of(12, 0))));
        server.verify();
    }

    @Test
    void unProfesionalQueNoExisteEsUn404() {
        server.expect(requestTo(BASE + "/api/professionals/999/schedules"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> client.findWeeklySlots(999L)).isInstanceOf(ProfessionalNotFoundException.class);
    }

    @Test
    void siElCatalogoFallaNoEstaDisponible() {
        server.expect(requestTo(BASE + "/api/professionals/202/schedules")).andRespond(withServerError());

        assertThatThrownBy(() -> client.findWeeklySlots(202L)).isInstanceOf(CatalogUnavailableException.class);
    }
}
