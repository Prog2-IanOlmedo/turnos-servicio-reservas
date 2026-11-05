package com.turnos.reservas.reservation.application.usecases;

import java.time.Instant;
import java.util.UUID;

import com.turnos.reservas.reservation.application.exception.InvalidPhoneNumberException;
import com.turnos.reservas.reservation.application.exception.PhoneNotRequestedException;
import com.turnos.reservas.reservation.domain.model.PhoneNumber;
import com.turnos.reservas.reservation.domain.model.PhoneSubmission;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.SubmitPhoneUseCase;
import com.turnos.reservas.reservation.domain.ports.out.PhoneSubmissionPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmitPhoneUseCaseImpl implements SubmitPhoneUseCase {

    private final PhoneSubmissionPublisher phoneSubmissionPublisher;

    // El estado local no cambia acá: la confirmación o el rechazo llegan después
    // por Kafka. Cada envío lleva un eventId nuevo (contrato 15.7).
    @Override
    public ReservationProcess submitPhone(ReservationProcess process, String rawPhoneNumber) {
        Instant now = Instant.now();
        if (!process.canSubmitPhone() || process.isOverdueAt(now)) {
            throw new PhoneNotRequestedException(process.getId(), process.getStatus());
        }
        PhoneNumber phoneNumber;
        try {
            phoneNumber = PhoneNumber.parse(rawPhoneNumber);
        } catch (IllegalArgumentException e) {
            throw new InvalidPhoneNumberException(e);
        }
        phoneSubmissionPublisher.publish(new PhoneSubmission(UUID.randomUUID().toString(),
                process.getReservationProcessId(), process.getHoldId(), process.getPhoneRequestEventId(),
                phoneNumber, now));
        return process;
    }
}
