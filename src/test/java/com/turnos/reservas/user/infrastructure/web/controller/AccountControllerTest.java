package com.turnos.reservas.user.infrastructure.web.controller;

import java.util.Set;

import com.turnos.reservas.shared.web.GlobalExceptionHandler;
import com.turnos.reservas.user.application.exception.EmailAlreadyUsedException;
import com.turnos.reservas.user.application.exception.InvalidCredentialsException;
import com.turnos.reservas.user.application.exception.LoginAlreadyUsedException;
import com.turnos.reservas.user.application.exception.UserNotFoundException;
import com.turnos.reservas.user.application.service.UserService;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.domain.model.UserRegistration;
import com.turnos.reservas.user.infrastructure.web.mapper.UserDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    private static final String REGISTRO = """
            {"login": "Ian", "password": "secreto123", "firstName": "Ian", "lastName": "Olmedo",
             "email": "ian@mail.com", "langKey": "es"}
            """;
    private static final String LOGIN = """
            {"username": "ian", "password": "secreto123", "rememberMe": true}
            """;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AccountController(userService, new UserDtoMapper()))
                .setControllerAdvice(new UserExceptionHandler(), new GlobalExceptionHandler())
                .build();
    }

    @Test
    void registraUnUsuarioNuevo() throws Exception {
        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
                .andExpect(status().isCreated());

        ArgumentCaptor<UserRegistration> datos = ArgumentCaptor.forClass(UserRegistration.class);
        verify(userService).register(datos.capture());
        assertThat(datos.getValue().login()).isEqualTo("ian");
        assertThat(datos.getValue().email()).isEqualTo("ian@mail.com");
    }

    @Test
    void rechazaUnRegistroConDatosInvalidos() throws Exception {
        String invalido = """
                {"login": "con espacios", "password": "abc", "email": "no-es-mail"}
                """;

        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest());
        verify(userService, never()).register(any());
    }

    @Test
    void unLoginRepetidoDevuelve400ConSuCode() throws Exception {
        when(userService.register(any())).thenThrow(new LoginAlreadyUsedException());

        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LOGIN_ALREADY_USED"));
    }

    @Test
    void unEmailRepetidoDevuelve400ConSuCode() throws Exception {
        when(userService.register(any())).thenThrow(new EmailAlreadyUsedException());

        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content(REGISTRO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"));
    }

    @Test
    void elLoginDevuelveElTokenEnElBodyYEnElHeader() throws Exception {
        when(userService.authenticate("ian", "secreto123", true)).thenReturn("jwt-firmado");

        mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(LOGIN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").value("jwt-firmado"))
                .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer jwt-firmado"));
    }

    @Test
    void credencialesIncorrectasDevuelven401() throws Exception {
        when(userService.authenticate("ian", "secreto123", true)).thenThrow(new InvalidCredentialsException());

        mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON).content(LOGIN))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void laCuentaSaleDelUsuarioAutenticadoYNoMuestraLaClave() throws Exception {
        User ian = User.builder().id(1L).login("ian").email("ian@mail.com").passwordHash("hash-argon2")
                .activated(true).authorities(Set.of(User.ROLE_USER)).build();
        when(userService.getAccount("ian")).thenReturn(ian);

        mockMvc.perform(get("/api/account").principal(() -> "ian"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("ian"))
                .andExpect(jsonPath("$.authorities[0]").value(User.ROLE_USER))
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void unaCuentaInexistenteDevuelve404() throws Exception {
        when(userService.getAccount("borrado")).thenThrow(new UserNotFoundException("borrado"));

        mockMvc.perform(get("/api/account").principal(() -> "borrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    void sinRememberMeElTokenDuraLoNormal() throws Exception {
        when(userService.authenticate("ian", "secreto123", false)).thenReturn("jwt-corto");

        mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"ian\", \"password\": \"secreto123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_token").value("jwt-corto"));
    }
}
