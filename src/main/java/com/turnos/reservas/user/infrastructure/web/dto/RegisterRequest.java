package com.turnos.reservas.user.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

// Mismos campos y largos que el registro de JHipster (sección 3.2).
// Sin toString para que la clave nunca termine en un log.
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[_.@A-Za-z0-9-]+$")
    private String login;
    @NotBlank
    @Size(min = 4, max = 100)
    private String password;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @NotBlank
    @Email
    @Size(min = 5, max = 254)
    private String email;
    @Size(max = 256)
    private String imageUrl;
    @Size(min = 2, max = 10)
    private String langKey;
}
