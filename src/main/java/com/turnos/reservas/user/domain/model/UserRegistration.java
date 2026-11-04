package com.turnos.reservas.user.domain.model;

import java.util.Locale;

// Datos que manda la app al registrarse. Login y email se guardan en minúsculas
// como en JHipster; si no viene idioma, uso español.
public record UserRegistration(
        String login,
        String password,
        String firstName,
        String lastName,
        String email,
        String imageUrl,
        String langKey) {

    public UserRegistration {
        login = login == null ? null : login.toLowerCase(Locale.ROOT);
        email = email == null ? null : email.toLowerCase(Locale.ROOT);
        langKey = langKey == null || langKey.isBlank() ? "es" : langKey;
    }
}
