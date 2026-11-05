package com.turnos.reservas.user.application.usecases;

import java.util.Optional;
import java.util.Set;

import com.turnos.reservas.user.application.exception.InvalidCredentialsException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import com.turnos.reservas.user.domain.ports.out.TokenIssuer;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    @InjectMocks
    private AuthenticateUserUseCaseImpl useCase;

    private User usuario(boolean activo) {
        return User.builder().id(1L).login("ian").passwordHash("hash-argon2")
                .activated(activo).authorities(Set.of(User.ROLE_USER)).build();
    }

    @Test
    void devuelveElTokenConCredencialesValidas() {
        User ian = usuario(true);
        when(userRepository.findByLogin("ian")).thenReturn(Optional.of(ian));
        when(passwordHasher.matches("secreto123", "hash-argon2")).thenReturn(true);
        when(tokenIssuer.issue(ian, true)).thenReturn("jwt-firmado");

        assertThat(useCase.authenticate("Ian", "secreto123", true)).isEqualTo("jwt-firmado");
    }

    @Test
    void rechazaUnaClaveIncorrecta() {
        when(userRepository.findByLogin("ian")).thenReturn(Optional.of(usuario(true)));
        when(passwordHasher.matches("otra", "hash-argon2")).thenReturn(false);

        assertThatThrownBy(() -> useCase.authenticate("ian", "otra", false))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(tokenIssuer, never()).issue(any(), anyBoolean());
    }

    @Test
    void rechazaUnUsuarioInexistenteConElMismoError() {
        when(userRepository.findByLogin("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.authenticate("nadie", "secreto123", false))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rechazaUnUsuarioInactivo() {
        when(userRepository.findByLogin("ian")).thenReturn(Optional.of(usuario(false)));

        assertThatThrownBy(() -> useCase.authenticate("ian", "secreto123", false))
                .isInstanceOf(InvalidCredentialsException.class);
        verify(passwordHasher, never()).matches(any(), any());
    }
}
