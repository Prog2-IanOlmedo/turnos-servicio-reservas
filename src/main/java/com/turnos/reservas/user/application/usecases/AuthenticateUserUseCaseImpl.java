package com.turnos.reservas.user.application.usecases;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import com.turnos.reservas.user.application.exception.InvalidCredentialsException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.in.AuthenticateUserUseCase;
import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import com.turnos.reservas.user.domain.ports.out.TokenIssuer;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class AuthenticateUserUseCaseImpl implements AuthenticateUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    // Hash de una clave al azar que nadie conoce. Se calcula una sola vez al arrancar.
    private final String dummyHash;

    public AuthenticateUserUseCaseImpl(UserRepository userRepository, PasswordHasher passwordHasher,
                                       TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.dummyHash = passwordHasher.hash(UUID.randomUUID().toString());
    }

    // Usuario inexistente, inactivo o clave incorrecta: los tres dan el mismo error y
    // tardan lo mismo. Sin un usuario activo igual calculo un Argon2 contra el hash
    // ficticio, así el tiempo de respuesta no revela qué usuarios existen.
    @Override
    public String authenticate(String login, String password, boolean rememberMe) {
        Optional<User> candidate = userRepository.findByLogin(login.toLowerCase(Locale.ROOT))
                .filter(User::isActivated);
        String hashToCheck = candidate.map(User::getPasswordHash).orElse(dummyHash);
        boolean passwordMatches = passwordHasher.matches(password, hashToCheck);
        User user = candidate.filter(found -> passwordMatches)
                .orElseThrow(InvalidCredentialsException::new);
        return tokenIssuer.issue(user, rememberMe);
    }
}
