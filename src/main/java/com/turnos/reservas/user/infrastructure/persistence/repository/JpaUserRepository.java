package com.turnos.reservas.user.infrastructure.persistence.repository;

import java.util.Optional;

import com.turnos.reservas.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);
}
