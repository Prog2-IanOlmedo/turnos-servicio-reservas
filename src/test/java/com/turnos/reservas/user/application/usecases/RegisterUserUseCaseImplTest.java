package com.turnos.reservas.user.application.usecases;

import com.turnos.reservas.user.application.exception.EmailAlreadyUsedException;
import com.turnos.reservas.user.application.exception.LoginAlreadyUsedException;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.domain.ports.out.PasswordHasher;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseImplTest {

    private final UserRegistration datos = new UserRegistration("Ian", "secreto123", "Ian", "Olmedo",
            "ian@mail.com", null, "es");

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserUseCaseImpl useCase;

    @Test
    void guardaElHashYNuncaLaClave() {
        when(userRepository.existsByLogin("ian")).thenReturn(false);
        when(userRepository.existsByEmail("ian@mail.com")).thenReturn(false);
        when(passwordHasher.hash("secreto123")).thenReturn("hash-argon2");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User user = useCase.register(datos);

        assertThat(user.getPasswordHash()).isEqualTo("hash-argon2").isNotEqualTo("secreto123");
        assertThat(user.getLogin()).isEqualTo("ian");
        assertThat(user.getCreatedDate()).isNotNull();
    }

    @Test
    void rechazaUnLoginRepetido() {
        when(userRepository.existsByLogin("ian")).thenReturn(true);

        assertThatThrownBy(() -> useCase.register(datos)).isInstanceOf(LoginAlreadyUsedException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void rechazaUnEmailRepetido() {
        when(userRepository.existsByLogin("ian")).thenReturn(false);
        when(userRepository.existsByEmail("ian@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.register(datos)).isInstanceOf(EmailAlreadyUsedException.class);
        verify(userRepository, never()).save(any());
    }
}
