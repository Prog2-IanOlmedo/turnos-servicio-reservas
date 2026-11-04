package com.turnos.reservas;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.domain.ports.out.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class UserPersistenceTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    void guardaYRecuperaUnUsuarioConSusRoles() {
        UserRegistration datos = new UserRegistration("persistencia", "no-se-guarda", "Ana", "Perez",
                "persistencia@mail.com", null, null);
        Instant ahora = Instant.now().truncatedTo(ChronoUnit.MICROS);

        User guardado = userRepository.save(User.register(datos, "hash-de-prueba", ahora));

        assertThat(guardado.getId()).isNotNull();
        User leido = userRepository.findByLogin("persistencia").orElseThrow();
        assertThat(leido.getPasswordHash()).isEqualTo("hash-de-prueba");
        assertThat(leido.getAuthorities()).containsExactly(User.ROLE_USER);
        assertThat(leido.isActivated()).isTrue();
        assertThat(leido.getCreatedDate()).isEqualTo(ahora);
        assertThat(userRepository.existsByLogin("persistencia")).isTrue();
        assertThat(userRepository.existsByEmail("persistencia@mail.com")).isTrue();
        assertThat(userRepository.existsByLogin("otro")).isFalse();
    }
}
