package com.turnos.reservas.reservation.domain.ports.in;

public interface ReconcileOverdueProcessesUseCase {
    // Devuelve cuántos procesos cambiaron.
    int reconcile();
}
