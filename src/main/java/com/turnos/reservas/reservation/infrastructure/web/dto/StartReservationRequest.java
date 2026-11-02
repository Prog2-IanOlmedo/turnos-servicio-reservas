package com.turnos.reservas.reservation.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

// Los largos de nombre y apellido son los que exige la cátedra al confirmar (sección 10).
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartReservationRequest {
    @NotNull
    private Long professionalId;
    @NotNull
    private LocalDate date;
    @NotNull
    private LocalTime startTime;
    @NotBlank
    @Size(min = 2, max = 100)
    private String patientFirstName;
    @NotBlank
    @Size(min = 2, max = 100)
    private String patientLastName;
}
