package com.turnos.reservas.user.application.usecases;

import java.time.Instant;

import com.turnos.reservas.user.application.exception.EmailAlreadyUsedException;
import com.turnos.reservas.user.application.exception.LoginAlreadyUsedException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.domain.ports.in.RegisterUserUseCase;
import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    // La clave en texto plano solo vive en este método: se guarda el hash.
    @Override
    @Transactional
    public User register(UserRegistration registration) {
        if (userRepository.existsByLogin(registration.login())) {
            throw new LoginAlreadyUsedException();
        }
        if (userRepository.existsByEmail(registration.email())) {
            throw new EmailAlreadyUsedException();
        }
        String passwordHash = passwordHasher.hash(registration.password());
        return userRepository.save(User.register(registration, passwordHash, Instant.now()));
    }
}
