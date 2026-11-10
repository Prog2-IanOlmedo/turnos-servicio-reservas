package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;

public interface SubmitPhoneUseCase {
    ReservationProcess submitPhone(ReservationProcess process, String rawPhoneNumber);
}
