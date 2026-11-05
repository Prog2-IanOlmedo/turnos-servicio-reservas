package com.turnos.reservas.user.application.usecases;

import java.util.Optional;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.in.GetAccountUseCase;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetAccountUseCaseImpl implements GetAccountUseCase {

    private final UserRepository userRepository;

    @Override
    public Optional<User> getByLogin(String login) {
        return userRepository.findByLogin(login);
    }
}
