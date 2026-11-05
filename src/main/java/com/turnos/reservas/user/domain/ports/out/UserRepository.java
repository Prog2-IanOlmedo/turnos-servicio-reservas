package com.turnos.reservas.user.domain.ports.out;

import java.util.Optional;

import com.turnos.reservas.user.domain.model.User;

public interface UserRepository {

    User save(User user);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}
