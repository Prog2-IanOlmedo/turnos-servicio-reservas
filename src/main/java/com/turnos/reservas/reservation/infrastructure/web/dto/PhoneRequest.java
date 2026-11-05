package com.turnos.reservas.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// El formato fino (7 a 15 dígitos) lo valida el dominio igual que la cátedra.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneRequest {
    @NotBlank
    @Size(max = 30)
    private String phoneNumber;
}
