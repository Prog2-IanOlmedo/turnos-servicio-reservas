package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.infrastructure.web.dto.CancelReservationRequest;
import com.turnos.reservas.reservation.infrastructure.web.dto.PhoneRequest;
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

import java.security.Principal;
import java.util.List;

// El paciente es el usuario autenticado: sale del JWT ya validado, nunca de la URL
// ni del body (sección 9). Su login es el externalPatientId ante la cátedra.
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationDtoMapper reservationDtoMapper;

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getReservations(Principal principal) {
        return ResponseEntity.ok(reservationService.getReservationsByPatient(principal.getName()).stream()
                .map(reservationDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(Principal principal, @PathVariable Long id) {
        return ResponseEntity.ok(reservationDtoMapper.toResponse(
                reservationService.getReservationForPatient(id, principal.getName())));
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> startReservation(Principal principal,
                                                                @Valid @RequestBody StartReservationRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationDtoMapper.toResponse(
                reservationService.startReservation(reservationDtoMapper.toRequest(principal.getName(), body))));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ReservationResponse> cancelReservation(
            Principal principal, @PathVariable Long id,
            @Valid @RequestBody(required = false) CancelReservationRequest body) {
        String reason = body != null ? body.getReason() : null;
        return ResponseEntity.ok(reservationDtoMapper.toResponse(
                reservationService.cancelReservation(id, principal.getName(), reason)));
    }

    // 202 y no 200: la confirmación o el rechazo llegan después por Kafka.
    @PostMapping("/{id}/phone")
    public ResponseEntity<ReservationResponse> submitPhone(Principal principal, @PathVariable Long id,
                                                           @Valid @RequestBody PhoneRequest body) {
        return ResponseEntity.accepted().body(reservationDtoMapper.toResponse(
                reservationService.submitPhone(id, principal.getName(), body.getPhoneNumber())));
    }
}
