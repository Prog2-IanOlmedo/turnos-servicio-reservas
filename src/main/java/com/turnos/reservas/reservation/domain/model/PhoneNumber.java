package com.turnos.reservas.reservation.domain.model;

import java.util.regex.Pattern;

// Mismas reglas que la cátedra (contrato 15.5): se sacan espacios, guiones,
// paréntesis y puntos, y lo que queda tiene que cumplir ^\+?[0-9]{7,15}$.
public record PhoneNumber(String value) {

    private static final Pattern SEPARATORS = Pattern.compile("[\\s\\-().]");
    private static final Pattern VALID = Pattern.compile("^\\+?\\d{7,15}$");

    public PhoneNumber {
        if (value == null || !VALID.matcher(value).matches()) {
            throw new IllegalArgumentException("Número de teléfono inválido");
        }
    }

    public static PhoneNumber parse(String raw) {
        return new PhoneNumber(raw == null ? null : SEPARATORS.matcher(raw).replaceAll(""));
    }
}
