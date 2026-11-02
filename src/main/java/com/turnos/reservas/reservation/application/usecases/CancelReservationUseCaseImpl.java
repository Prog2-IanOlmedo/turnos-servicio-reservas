package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.application.exception.ReservationNotCancellableException;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.CancelReservationUseCase;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelReservationUseCaseImpl implements CancelReservationUseCase {

    private final AppointmentGateway appointmentGateway;
    private final ReservationProcessRepository reservationProcessRepository;

    // Igual que en la cátedra, cancelar algo ya cancelado no es error:
    // devuelvo el estado actual sin repetir el pedido (sección 12).
    @Override
    public ReservationProcess cancel(ReservationProcess process, String reason) {
        if (process.isCancelled()) {
            return process;
        }
        if (!process.canBeCancelled()) {
            throw new ReservationNotCancellableException(process.getId(), process.getStatus());
        }
        appointmentGateway.cancel(process.getReservationProcessId(), reason);
        process.cancel(reason);
        return reservationProcessRepository.save(process);
    }
}
