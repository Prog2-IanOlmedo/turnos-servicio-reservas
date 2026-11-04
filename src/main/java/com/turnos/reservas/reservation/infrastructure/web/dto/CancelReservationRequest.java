package com.turnos.reservas.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

// El motivo es opcional y la cátedra admite hasta 500 caracteres (sección 12).
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationRequest {
    @Size(max = 500)
    private String reason;
}
