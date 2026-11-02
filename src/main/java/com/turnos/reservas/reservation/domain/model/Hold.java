package com.turnos.reservas.reservation.domain.model;

import java.time.Instant;

// Lo que devuelve la cátedra al tomar un turno (contrato, sección 9).
// La vigencia real es expiresAt: guardarlo de este lado no la extiende.
public record Hold(String holdId, String reservationProcessId, Instant expiresAt) {
}
