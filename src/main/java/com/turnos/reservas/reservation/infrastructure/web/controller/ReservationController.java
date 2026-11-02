package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.infrastructure.web.dto.CancelReservationRequest;
import com.turnos.reservas.reservation.infrastructure.web.dto.ReservationResponse;
import com.turnos.reservas.reservation.infrastructure.web.dto.StartReservationRequest;
import com.turnos.reservas.reservation.infrastructure.web.mapper.ReservationDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<ReservationResponse> startReservation(@PathVariable String patientId,
                                                                @Valid @RequestBody StartReservationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDtoMapper.toResponse(
                reservationService.startReservation(reservationDtoMapper.toRequest(patientId, body))));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable String patientId, @PathVariable Long id,
            @Valid @RequestBody(required = false) CancelReservationRequest body) {
        String reason = body != null ? body.getReason() : null;
        return ResponseEntity.ok(reservationDtoMapper.toResponse(
                reservationService.cancelReservation(id, patientId, reason)));
    }
}
