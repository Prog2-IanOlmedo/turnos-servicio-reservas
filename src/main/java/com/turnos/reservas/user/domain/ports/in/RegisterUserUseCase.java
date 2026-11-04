package com.turnos.reservas.user.domain.ports.in;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;

public interface RegisterUserUseCase {
    User register(UserRegistration registration);
}
