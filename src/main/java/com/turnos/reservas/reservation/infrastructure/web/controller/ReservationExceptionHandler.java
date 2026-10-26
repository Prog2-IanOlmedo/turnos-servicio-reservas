package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.shared.web.ApiProblem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Va antes que el handler general, que también atrapa Exception.
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ReservationController.class)
public class ReservationExceptionHandler {

    @ExceptionHandler(ReservationNotFoundException.class)
    public ProblemDetail handleReservationNotFound(ReservationNotFoundException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.NOT_FOUND, "RESERVATION_NOT_FOUND", ex.getMessage(), request.getRequestURI());
    }
}
