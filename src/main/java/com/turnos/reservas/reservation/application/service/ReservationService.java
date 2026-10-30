package com.turnos.reservas.reservation.application.service;

import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.ApplyReservationEventUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationByIdUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationsByPatientUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final GetReservationsByPatientUseCase getReservationsByPatientUseCase;
    private final GetReservationByIdUseCase getReservationByIdUseCase;
    private final ApplyReservationEventUseCase applyReservationEventUseCase;

    public List<ReservationProcess> getReservationsByPatient(String externalPatientId) {
        return getReservationsByPatientUseCase.getReservationsByPatient(externalPatientId);
    }

    // Una reserva de otro paciente se responde igual que una inexistente,
    // para no revelar que existe.
    public ReservationProcess getReservationForPatient(Long id, String externalPatientId) {
        return getReservationByIdUseCase.getReservationById(id)
                .filter(process -> process.belongsTo(externalPatientId))
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public boolean applyEvent(ReservationEvent event) {
        return applyReservationEventUseCase.applyEvent(event);
    }
}
