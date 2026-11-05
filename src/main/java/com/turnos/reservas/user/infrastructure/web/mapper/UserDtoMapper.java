package com.turnos.reservas.user.infrastructure.web.mapper;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.infrastructure.web.dto.AccountResponse;
import com.turnos.reservas.user.infrastructure.web.dto.RegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserRegistration toRegistration(RegisterRequest body) {
        return new UserRegistration(body.getLogin(), body.getPassword(), body.getFirstName(),
                body.getLastName(), body.getEmail(), body.getImageUrl(), body.getLangKey());
    }

    public AccountResponse toAccountResponse(User user) {
        if (user == null) {
            return null;
        }
        return AccountResponse.builder()
                .id(user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .activated(user.isActivated())
                .langKey(user.getLangKey())
                .createdDate(user.getCreatedDate())
                .authorities(user.getAuthorities())
                .build();
    }
}
