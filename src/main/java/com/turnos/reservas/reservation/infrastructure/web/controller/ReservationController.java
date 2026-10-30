package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.infrastructure.web.dto.ReservationResponse;
import com.turnos.reservas.reservation.infrastructure.web.mapper.ReservationDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// El patientId va en la URL hasta que haya seguridad; después saldrá del JWT
// del usuario autenticado.
@RestController
@RequestMapping("/api/patients/{patientId}/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationDtoMapper reservationDtoMapper;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(@PathVariable String patientId) {
        return ResponseEntity.ok(reservationService.getReservationsByPatient(patientId).stream()
                .map(reservationDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String patientId, @PathVariable Long id) {
        return ResponseEntity.ok(reservationDtoMapper.toResponse(
                reservationService.getReservationForPatient(id, patientId)));
    }
}
