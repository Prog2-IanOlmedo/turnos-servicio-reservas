package com.turnos.reservas.user.infrastructure.web.controller;

import com.turnos.reservas.shared.web.ApiProblem;
import com.turnos.reservas.user.application.exception.EmailAlreadyUsedException;
import com.turnos.reservas.user.application.exception.InvalidCredentialsException;
import com.turnos.reservas.user.application.exception.LoginAlreadyUsedException;
import com.turnos.reservas.user.application.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Va antes que el handler general, que también atrapa Exception.
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = AccountController.class)
public class UserExceptionHandler {

    @ExceptionHandler(LoginAlreadyUsedException.class)
    public ProblemDetail handleLoginUsed(LoginAlreadyUsedException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.BAD_REQUEST, "LOGIN_ALREADY_USED", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ProblemDetail handleEmailUsed(EmailAlreadyUsedException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.BAD_REQUEST, "EMAIL_ALREADY_USED", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(InvalidCredentialsException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return ApiProblem.of(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage(), request.getRequestURI());
    }
}
