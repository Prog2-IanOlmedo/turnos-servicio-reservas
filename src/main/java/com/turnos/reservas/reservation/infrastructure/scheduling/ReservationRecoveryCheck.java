package com.turnos.reservas.reservation.infrastructure.scheduling;

import com.turnos.reservas.reservation.application.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// Recupera procesos que quedaron abiertos (corte, reinicio o evento perdido).
// La primera pasada es al arrancar (initialDelay) y después corre cada intervalo.
// Si la cátedra no responde no cierro nada: se reintenta en la próxima pasada.
@Slf4j
@Component
public class ReservationRecoveryCheck {

    private final ReservationService reservationService;
    private final boolean enabled;

    public ReservationRecoveryCheck(ReservationService reservationService,
                                    @Value("${reservas.recovery.enabled:true}") boolean enabled) {
        this.reservationService = reservationService;
        this.enabled = enabled;
    }

    @Scheduled(initialDelayString = "${reservas.recovery.initial-delay-ms:15000}",
            fixedDelayString = "${reservas.recovery.interval-ms:60000}")
    public void check() {
        if (!enabled) {
            return;
        }
        try {
            int changed = reservationService.reconcileOverdueProcesses();
            if (changed > 0) {
                log.info("Recuperación: {} procesos reconciliados con la cátedra", changed);
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo reconciliar con la cátedra, reintento en la próxima pasada: {}", e.getMessage());
        }
    }
}
