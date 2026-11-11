package com.turnos.reservas.reservation.application.service;

import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.reservation.domain.model.ReservationEvent;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.domain.ports.in.CancelReservationUseCase;
import com.turnos.reservas.reservation.domain.ports.in.StartReservationUseCase;
import com.turnos.reservas.reservation.domain.ports.in.SubmitPhoneUseCase;
import com.turnos.reservas.reservation.domain.ports.in.ApplyReservationEventUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationByIdUseCase;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationsByPatientUseCase;
import com.turnos.reservas.reservation.domain.ports.in.ReconcileOverdueProcessesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReconcileOverdueProcessesUseCase reconcileOverdueProcessesUseCase;
    private final GetReservationsByPatientUseCase getReservationsByPatientUseCase;
    private final GetReservationByIdUseCase getReservationByIdUseCase;
    private final ApplyReservationEventUseCase applyReservationEventUseCase;
    private final StartReservationUseCase startReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;
    private final SubmitPhoneUseCase submitPhoneUseCase;

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

    public ReservationProcess startReservation(ReservationRequest request) {
        return startReservationUseCase.start(request);
    }

    // Solo el dueño puede cancelar: una reserva ajena se responde como inexistente.
    public ReservationProcess cancelReservation(Long id, String externalPatientId, String reason) {
        return cancelReservationUseCase.cancel(getReservationForPatient(id, externalPatientId), reason);
    }

    // Igual que al cancelar: solo el dueño puede responder el pedido de teléfono.
    public ReservationProcess submitPhone(Long id, String externalPatientId, String phoneNumber) {
        return submitPhoneUseCase.submitPhone(getReservationForPatient(id, externalPatientId), phoneNumber);
    }

    public int reconcileOverdueProcesses() {
        return reconcileOverdueProcessesUseCase.reconcile();
    }
}
