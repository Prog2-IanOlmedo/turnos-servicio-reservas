package com.turnos.reservas.reservation.domain.ports.out;

public interface ProcessedEventRepository {

    boolean isProcessed(String eventId);
    void markProcessed(String eventId);

}
