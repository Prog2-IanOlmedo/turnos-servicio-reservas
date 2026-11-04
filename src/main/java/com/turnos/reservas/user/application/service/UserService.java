package com.turnos.reservas.user.application.service;

import com.turnos.reservas.user.application.exception.UserNotFoundException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.domain.ports.in.AuthenticateUserUseCase;
import com.turnos.reservas.user.domain.ports.in.GetAccountUseCase;
import com.turnos.reservas.user.domain.ports.in.RegisterUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RegisterUserUseCase registerUserUseCase;
    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final GetAccountUseCase getAccountUseCase;

    public User register(UserRegistration registration) {
        return registerUserUseCase.register(registration);
    }

    public String authenticate(String login, String password, boolean rememberMe) {
        return authenticateUserUseCase.authenticate(login, password, rememberMe);
    }

    public User getAccount(String login) {
        return getAccountUseCase.getByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));
    }
}
