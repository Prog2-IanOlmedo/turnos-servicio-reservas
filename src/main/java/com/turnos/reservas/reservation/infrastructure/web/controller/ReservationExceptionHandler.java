package com.turnos.reservas.reservation.infrastructure.web.controller;

import com.turnos.reservas.reservation.application.exception.CatedraRejectedException;
import com.turnos.reservas.reservation.application.exception.CatedraUnavailableException;
import com.turnos.reservas.reservation.application.exception.ReservationNotCancellableException;
import com.turnos.reservas.reservation.application.exception.ReservationNotFoundException;
import com.turnos.reservas.shared.web.ApiProblem;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    @ExceptionHandler(ReservationNotCancellableException.class)
    public ProblemDetail handleNotCancellable(ReservationNotCancellableException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.CONFLICT, "RESERVATION_NOT_CANCELLABLE", ex.getMessage(), request.getRequestURI());
    }

    // Un rechazo de la cátedra se pasa a la app con su mismo status y code
    // (por ejemplo 409 SLOT_ALREADY_HELD). Un 401/403 es problema de nuestra
    // credencial técnica, no del pedido del usuario: sale como 502 sin detalles.
    @ExceptionHandler(CatedraRejectedException.class)
    public ProblemDetail handleCatedraRejected(CatedraRejectedException ex, HttpServletRequest request) {
        if (ex.getStatus() == 401 || ex.getStatus() == 403) {
            return ApiProblem.of(HttpStatus.BAD_GATEWAY, "CATEDRA_AUTH_ERROR",
                    "No se pudo operar con el servicio de turnos.", request.getRequestURI());
        }
        String code = ex.getCode() != null ? ex.getCode() : "CATEDRA_REJECTED";
        return ApiProblem.of(HttpStatusCode.valueOf(ex.getStatus()), code, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(CatedraUnavailableException.class)
    public ProblemDetail handleCatedraUnavailable(CatedraUnavailableException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.SERVICE_UNAVAILABLE, "CATEDRA_UNAVAILABLE",
                "El servicio de turnos no está disponible. Probá de nuevo en unos minutos.", request.getRequestURI());
    }
}
