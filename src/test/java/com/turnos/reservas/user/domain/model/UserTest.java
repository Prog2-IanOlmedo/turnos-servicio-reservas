package com.turnos.reservas.user.domain.model;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void alRegistrarseQuedaActivoYSoloConRolUser() {
        UserRegistration datos = new UserRegistration("Ian.Olmedo", "secreto123", "Ian", "Olmedo",
                "IAN@MAIL.COM", null, null);
        Instant ahora = Instant.parse("2026-11-04T19:00:00Z");

        User user = User.register(datos, "hash-argon2", ahora);

        assertThat(user.isActivated()).isTrue();
        assertThat(user.getAuthorities()).containsExactly(User.ROLE_USER);
        assertThat(user.getLogin()).isEqualTo("ian.olmedo");
        assertThat(user.getEmail()).isEqualTo("ian@mail.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash-argon2");
        assertThat(user.getFirstName()).isEqualTo("Ian");
        assertThat(user.getLastName()).isEqualTo("Olmedo");
        assertThat(user.getLangKey()).isEqualTo("es");
        assertThat(user.getCreatedDate()).isEqualTo(ahora);
        assertThat(user.getId()).isNull();
    }

    @Test
    void respetaElIdiomaElegido() {
        UserRegistration datos = new UserRegistration("ian", "secreto123", null, null,
                "ian@mail.com", "https://img.test/ian.png", "en");

        assertThat(datos.langKey()).isEqualTo("en");
        assertThat(datos.imageUrl()).isEqualTo("https://img.test/ian.png");
    }

    @Test
    void unIdiomaEnBlancoPasaAEspanol() {
        UserRegistration datos = new UserRegistration("ian", "secreto123", null, null, "ian@mail.com", null, " ");

        assertThat(datos.langKey()).isEqualTo("es");
    }

    @Test
    void toleraLoginYEmailNulosParaQueLosValideLaWeb() {
        UserRegistration datos = new UserRegistration(null, null, null, null, null, null, null);

        assertThat(datos.login()).isNull();
        assertThat(datos.email()).isNull();
    }
}
