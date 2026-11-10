package com.turnos.reservas.reservation.application.usecases;

import java.time.Instant;
import java.util.Objects;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationRequest;
import com.turnos.reservas.reservation.domain.ports.in.StartReservationUseCase;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Diagrama de la sección 17: hold y confirmación inicial en el mismo paso.
// El proceso se guarda apenas existe el hold, así sus ids no se pierden
// si la confirmación falla o se corta (sección 18.4).
// Sin @Transactional: son llamadas HTTP y cada guardado vale por sí solo.
@Component
@RequiredArgsConstructor
public class StartReservationUseCaseImpl implements StartReservationUseCase {

    private static final String HOLD_EXPIRED = "HOLD_EXPIRED";

    private final AppointmentGateway appointmentGateway;
    private final ReservationProcessRepository reservationProcessRepository;

    @Override
    public ReservationProcess start(ReservationRequest request) {
        Hold hold = appointmentGateway.createHold(request.professionalId(), request.date(),
                request.startTime(), request.externalPatientId());
        // save no debería devolver null; si pasara, mejor un error claro acá que un NPE más abajo.
        ReservationProcess process = Objects.requireNonNull(
                reservationProcessRepository.save(ReservationProcess.startFrom(hold, request)),
                "No se pudo guardar el proceso de reserva");
        try {
            Instant phoneDeadline = appointmentGateway.confirmHold(hold.holdId(), request.externalPatientId(),
                    request.patientFirstName(), request.patientLastName());
            // Kafka pudo haber registrado el pedido de teléfono mientras esperábamos
            // esta respuesta REST: releo el proceso para no pisar ese dato.
            ReservationProcess current = reservationProcessRepository.findById(process.getId()).orElse(process);
            current.markWaitingForPhone(phoneDeadline);
            return reservationProcessRepository.save(current);
        } catch (CatedraRejectedException e) {
            // Sección 18.3: si el hold venció se cierra el proceso local.
            if (e.hasCode(HOLD_EXPIRED)) {
                process.expire(HOLD_EXPIRED);
                reservationProcessRepository.save(process);
            }
            throw e;
        }
    }
}
