package com.turnos.reservas.user.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// Mismo body que POST /api/authenticate de JHipster.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(max = 50)
    private String username;
    @NotBlank
    @Size(max = 100)
    private String password;
    // Opcional como en JHipster: si no viene, el token dura lo normal.
    private Boolean rememberMe;
}
