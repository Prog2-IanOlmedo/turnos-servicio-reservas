package com.turnos.reservas.user.domain.ports.out;

import com.turnos.reservas.user.domain.model.User;

public interface TokenIssuer {

    String issue(User user, boolean rememberMe);
}
