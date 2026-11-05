package com.turnos.reservas.user.infrastructure.persistence.adapter;

import java.util.Optional;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import com.turnos.reservas.user.infrastructure.persistence.mapper.UserMapper;
import com.turnos.reservas.user.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaUserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        return userMapper.toDomainModel(jpaUserRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return jpaUserRepository.findByLogin(login).map(userMapper::toDomainModel);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaUserRepository.existsByLogin(login);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }
}
