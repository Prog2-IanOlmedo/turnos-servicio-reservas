package com.turnos.reservas.reservation.infrastructure.messaging;

import java.util.Optional;

import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.infrastructure.messaging.dto.CatedraReservationEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

// Topic de acciones de la cátedra. La entrega es "al menos una vez" (15.1): un duplicado
// lo descarta applyEvent por eventId. Un mensaje ilegible o de un tipo desconocido se
// registra y se saltea, para no trabar la partición.
@Slf4j
@Component
@RequiredArgsConstructor
public class CatedraReservationEventListener {

    private final ReservationService reservationService;
    private final CatedraEventMapper catedraEventMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${catedra.kafka.actions-topic}")
    public void onMessage(String payload) {
        Optional<ReservationEvent> event;
        try {
            event = catedraEventMapper.toDomain(objectMapper.readValue(payload, CatedraReservationEventDto.class));
        } catch (JacksonException e) {
            log.warn("Mensaje de la cátedra ilegible, se ignora: {}", e.getOriginalMessage());
            return;
        }
        event.ifPresentOrElse(this::apply,
                () -> log.warn("Evento de la cátedra de tipo desconocido o incompleto, se ignora"));
    }

    private void apply(ReservationEvent event) {
        boolean changed = reservationService.applyEvent(event);
        log.info("Evento {} ({}) del proceso {}: {}", event.getEventId(), event.getType(),
                event.getReservationProcessId(), changed ? "aplicado" : "sin cambios");
    }
}
