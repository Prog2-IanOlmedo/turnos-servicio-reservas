package com.turnos.reservas.user.domain.ports.in;

import java.util.Optional;

import com.turnos.reservas.user.domain.model.User;

public interface GetAccountUseCase {
    Optional<User> getByLogin(String login);
}
