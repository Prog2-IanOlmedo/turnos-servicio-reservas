package com.turnos.reservas.user.application.usecases;

import java.util.Locale;

import com.turnos.reservas.user.application.exception.InvalidCredentialsException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.in.AuthenticateUserUseCase;
import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import com.turnos.reservas.user.domain.ports.out.TokenIssuer;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    // Usuario inexistente, inactivo o clave incorrecta: todos dan el mismo error.
    @Override
    public String authenticate(String login, String password, boolean rememberMe) {
        User user = userRepository.findByLogin(login.toLowerCase(Locale.ROOT))
                .filter(User::isActivated)
                .filter(found -> passwordHasher.matches(password, found.getPasswordHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return tokenIssuer.issue(user, rememberMe);
    }
}
