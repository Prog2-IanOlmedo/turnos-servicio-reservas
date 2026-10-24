package com.turnos.reservas.reservation.domain.ports.in;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;

import java.util.List;

public interface GetReservationsByPatientUseCase {
    List<ReservationProcess> getReservationsByPatient(String externalPatientId);
}
