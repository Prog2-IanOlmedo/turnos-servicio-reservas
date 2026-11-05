package com.turnos.reservas.user.infrastructure.persistence.mapper;

import java.util.HashSet;
import java.util.Set;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .id(user.getId())
                .login(user.getLogin())
                .passwordHash(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .activated(user.isActivated())
                .langKey(user.getLangKey())
                .createdDate(user.getCreatedDate())
                .authorities(new HashSet<>(user.getAuthorities()))
                .build();
    }

    public User toDomainModel(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .passwordHash(entity.getPasswordHash())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .imageUrl(entity.getImageUrl())
                .activated(entity.isActivated())
                .langKey(entity.getLangKey())
                .createdDate(entity.getCreatedDate())
                .authorities(Set.copyOf(entity.getAuthorities()))
                .build();
    }
}
