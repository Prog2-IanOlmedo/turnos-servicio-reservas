package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationsByPatientUseCase;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetReservationsByPatientUseCaseImpl implements GetReservationsByPatientUseCase {

    private final ReservationProcessRepository repository;

    @Override
    public List<ReservationProcess> getReservationsByPatient(String externalPatientId) {
        return repository.findByExternalPatientId(externalPatientId);
    }
}
