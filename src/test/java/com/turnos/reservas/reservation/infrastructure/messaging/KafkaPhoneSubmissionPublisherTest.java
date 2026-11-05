package com.turnos.reservas.reservation.infrastructure.messaging;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import com.jayway.jsonpath.JsonPath;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.PhoneNumber;
import com.turnos.reservas.reservation.domain.model.PhoneSubmission;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPhoneSubmissionPublisherTest {

    private static final String TOPIC = "catedra.turnos.telefono.proyecto-ian-olmedo";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private KafkaPhoneSubmissionPublisher publisher;

    private final PhoneSubmission envio = new PhoneSubmission("phone-submitted-b91e", "process-1", "hold-1",
            "request-event-8a31", PhoneNumber.parse("+5492615551234"), Instant.parse("2026-11-09T15:10:00Z"));

    @BeforeEach
    void setUp() {
        publisher = new KafkaPhoneSubmissionPublisher(kafkaTemplate, JsonMapper.builder().build(),
                TOPIC, "proyecto-ian-olmedo", Duration.ofMillis(100));
    }

    @Test
    void mandaElEventoDelContratoConElProcesoComoKey() {
        when(kafkaTemplate.send(eq(TOPIC), eq("process-1"), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        publisher.publish(envio);

        ArgumentCaptor<String> payload = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(TOPIC), eq("process-1"), payload.capture());
        String json = payload.getValue();
        assertThat((String) JsonPath.read(json, "$.eventType")).isEqualTo("AdditionalInformationSubmitted");
        assertThat((Integer) JsonPath.read(json, "$.schemaVersion")).isEqualTo(1);
        assertThat((String) JsonPath.read(json, "$.eventId")).isEqualTo("phone-submitted-b91e");
        assertThat((String) JsonPath.read(json, "$.groupId")).isEqualTo("proyecto-ian-olmedo");
        assertThat((String) JsonPath.read(json, "$.reservationProcessId")).isEqualTo("process-1");
        assertThat((String) JsonPath.read(json, "$.holdId")).isEqualTo("hold-1");
        assertThat((String) JsonPath.read(json, "$.requestEventId")).isEqualTo("request-event-8a31");
        assertThat((String) JsonPath.read(json, "$.information.phoneNumber")).isEqualTo("+5492615551234");
        assertThat((String) JsonPath.read(json, "$.occurredAt")).isEqualTo("2026-11-09T15:10:00Z");
    }

    @Test
    void siKafkaRechazaElEnvioSeInformaComoNoDisponible() {
        CompletableFuture<SendResult<String, String>> fallido =
                CompletableFuture.failedFuture(new IllegalStateException("broker caído"));
        when(kafkaTemplate.send(eq(TOPIC), eq("process-1"), anyString())).thenReturn(fallido);

        assertThatThrownBy(() -> publisher.publish(envio)).isInstanceOf(CatedraUnavailableException.class);
    }

    @Test
    void siKafkaNoContestaATiempoSeInformaComoNoDisponible() {
        when(kafkaTemplate.send(eq(TOPIC), eq("process-1"), anyString())).thenReturn(new CompletableFuture<>());

        assertThatThrownBy(() -> publisher.publish(envio)).isInstanceOf(CatedraUnavailableException.class);
    }

    @Test
    void siElProductorFallaEnElMomentoSeInformaComoNoDisponible() {
        when(kafkaTemplate.send(eq(TOPIC), eq("process-1"), anyString())).thenThrow(new KafkaException("sin broker"));

        assertThatThrownBy(() -> publisher.publish(envio)).isInstanceOf(CatedraUnavailableException.class);
    }
}
