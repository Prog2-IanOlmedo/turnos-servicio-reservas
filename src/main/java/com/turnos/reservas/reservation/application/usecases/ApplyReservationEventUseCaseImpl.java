package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.ApplyReservationEventUseCase;
import com.turnos.reservas.reservation.domain.ports.out.ProcessedEventRepository;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplyReservationEventUseCaseImpl implements ApplyReservationEventUseCase {

    private final ReservationProcessRepository processRepository;
    private final ProcessedEventRepository processedEventRepository;

    // Devuelve true solo si el evento cambió el estado del proceso.
    @Override
    @Transactional
    public boolean applyEvent(ReservationEvent event) {
        if (processedEventRepository.isProcessed(event.getEventId())) {
            return false;
        }
        boolean changed = processRepository.findByReservationProcessId(event.getReservationProcessId())
                .map(process -> applyAndSave(event, process))
                .orElseGet(() -> {
                    log.warn("Evento {} de un proceso desconocido: {}",
                            event.getEventId(), event.getReservationProcessId());
                    return false;
                });
        processedEventRepository.markProcessed(event.getEventId());
        return changed;
    }

    private boolean applyAndSave(ReservationEvent event, ReservationProcess process) {
        boolean changed = event.applyTo(process);
        if (changed) {
            processRepository.save(process);
        }
        return changed;
    }
}
