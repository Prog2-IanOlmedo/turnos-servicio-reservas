package com.turnos.reservas.user.domain.ports.in;

public interface AuthenticateUserUseCase {
    String authenticate(String login, String password, boolean rememberMe);
}
