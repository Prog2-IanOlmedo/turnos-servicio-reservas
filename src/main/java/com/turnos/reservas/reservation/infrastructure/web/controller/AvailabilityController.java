package com.turnos.reservas.reservation.infrastructure.web.controller;

import java.time.LocalDate;

import com.turnos.reservas.reservation.application.exception.CatalogUnavailableException;
import com.turnos.reservas.reservation.application.exception.ProfessionalNotFoundException;
import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.infrastructure.web.dto.AvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professionals")
@RequiredArgsConstructor
public class AvailabilityController {

    private final ReservationService reservationService;

    @GetMapping("/{id}/availability")
    public ResponseEntity<AvailabilityResponse> getAvailability(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(AvailabilityResponse.from(reservationService.getAvailability(id, date)));
    }

    @ExceptionHandler(ProfessionalNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProfessionalNotFound(ProfessionalNotFoundException e) {
        return problem(HttpStatus.NOT_FOUND, e.getMessage(), "PROFESSIONAL_NOT_FOUND");
    }

    @ExceptionHandler(CatalogUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleCatalogUnavailable(CatalogUnavailableException e) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage(), "CATALOG_UNAVAILABLE");
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String detail, String code) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty("code", code);
        return ResponseEntity.status(status).body(problem);
    }
}
