package com.turnos.reservas.reservation.infrastructure.messaging;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.domain.model.PhoneSubmission;
import com.turnos.reservas.reservation.domain.ports.out.PhoneSubmissionPublisher;
import com.turnos.reservas.reservation.infrastructure.messaging.dto.PhoneSubmittedEventDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

// Publica AdditionalInformationSubmitted en el topic de teléfono (contrato 15.5).
// La key es el reservationProcessId para que la cátedra conserve el orden del proceso.
// Espero la confirmación de Kafka: si no llega, el usuario se entera y puede reintentar.
// El teléfono no se escribe en el log.
@Slf4j
@Component
public class KafkaPhoneSubmissionPublisher implements PhoneSubmissionPublisher {

    private static final String EVENT_TYPE = "AdditionalInformationSubmitted";
    private static final int SCHEMA_VERSION = 1;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String phoneTopic;
    private final String groupId;
    private final Duration sendTimeout;

    public KafkaPhoneSubmissionPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                         ObjectMapper objectMapper,
                                         @Value("${catedra.kafka.phone-topic}") String phoneTopic,
                                         @Value("${catedra.group-id}") String groupId,
                                         @Value("${catedra.kafka.send-timeout:10s}") Duration sendTimeout) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.phoneTopic = phoneTopic;
        this.groupId = groupId;
        this.sendTimeout = sendTimeout;
    }

    @Override
    public void publish(PhoneSubmission submission) {
        PhoneSubmittedEventDto event = new PhoneSubmittedEventDto(
                submission.eventId(), EVENT_TYPE, groupId,
                submission.reservationProcessId(), submission.holdId(), submission.requestEventId(),
                new PhoneSubmittedEventDto.Information(submission.phoneNumber().value()),
                submission.occurredAt().toString(), SCHEMA_VERSION);
        String payload = objectMapper.writeValueAsString(event);
        try {
            kafkaTemplate.send(phoneTopic, submission.reservationProcessId(), payload)
                    .get(sendTimeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CatedraUnavailableException("Se interrumpió el envío del teléfono", e);
        } catch (ExecutionException | TimeoutException | KafkaException e) {
            throw new CatedraUnavailableException("No se pudo mandar el teléfono a la cátedra", e);
        }
        log.info("Teléfono enviado para el proceso {} (evento {})",
                submission.reservationProcessId(), submission.eventId());
    }
}
