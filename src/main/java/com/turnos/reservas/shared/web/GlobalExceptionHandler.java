package com.turnos.reservas.shared.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// Errores comunes a todos los slices. No importa nada de ningún slice.
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Último recurso: el detalle va al log, al cliente no se le muestra nada interno.
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return ApiProblem.of(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Ocurrió un error inesperado.", request.getRequestURI());
    }

    // Los errores que resuelve Spring también llevan un code estable.
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatusCode statusCode, WebRequest request) {
        if (body instanceof ProblemDetail problem
                && (problem.getProperties() == null || !problem.getProperties().containsKey("code"))) {
            HttpStatus status = HttpStatus.resolve(statusCode.value());
            problem.setProperty("code", status != null ? status.name() : "HTTP_" + statusCode.value());
        }
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }
}
