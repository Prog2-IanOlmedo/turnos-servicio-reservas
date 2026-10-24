package com.turnos.reservas.reservation.application.usecases;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.GetReservationByIdUseCase;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetReservationByIdUseCaseImpl implements GetReservationByIdUseCase {

    private final ReservationProcessRepository repository;

    @Override
    public Optional<ReservationProcess> getReservationById(Long id) {
        return repository.findById(id);
    }
}
