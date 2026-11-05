package com.turnos.reservas.user.application.service;

import java.util.Optional;

import com.turnos.reservas.user.application.exception.UserNotFoundException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.domain.ports.in.AuthenticateUserUseCase;
import com.turnos.reservas.user.domain.ports.in.GetAccountUseCase;
import com.turnos.reservas.user.domain.ports.in.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private RegisterUserUseCase registerUserUseCase;

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    @Mock
    private GetAccountUseCase getAccountUseCase;

    @InjectMocks
    private UserService service;

    private final User ian = User.builder().id(1L).login("ian").build();

    @Test
    void registrarDelegaEnElCasoDeUso() {
        UserRegistration datos = new UserRegistration("ian", "secreto123", null, null, "ian@mail.com", null, null);
        when(registerUserUseCase.register(datos)).thenReturn(ian);

        assertThat(service.register(datos)).isSameAs(ian);
    }

    @Test
    void autenticarDelegaEnElCasoDeUso() {
        when(authenticateUserUseCase.authenticate("ian", "secreto123", false)).thenReturn("jwt");

        assertThat(service.authenticate("ian", "secreto123", false)).isEqualTo("jwt");
    }

    @Test
    void devuelveLaCuentaDelUsuario() {
        when(getAccountUseCase.getByLogin("ian")).thenReturn(Optional.of(ian));

        assertThat(service.getAccount("ian")).isSameAs(ian);
    }

    @Test
    void unaCuentaInexistenteEsUnError() {
        when(getAccountUseCase.getByLogin("nadie")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAccount("nadie"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("nadie");
    }
}
