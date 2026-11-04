package com.turnos.reservas;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Set;

import javax.crypto.spec.SecretKeySpec;

import com.jayway.jsonpath.JsonPath;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.turnos.reservas.user.domain.model.User;
import com.turnos.reservas.user.infrastructure.security.JwtTokenIssuer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Con la cadena de seguridad real: registro, login y acceso con y sin token.
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SecurityIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private String registrarYLoguear(String login) throws Exception {
        mockMvc.perform(post("/api/register").contentType(MediaType.APPLICATION_JSON).content("""
                        {"login": "%s", "password": "secreto123", "firstName": "Ana",
                         "lastName": "Perez", "email": "%s@mail.com"}
                        """.formatted(login, login)))
                .andExpect(status().isCreated());

        String respuesta = mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"%s\", \"password\": \"secreto123\"}".formatted(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(respuesta, "$.id_token");
    }

    @Test
    void registroLoginYCuentaDePuntaAPunta() throws Exception {
        String token = registrarYLoguear("integracion");

        mockMvc.perform(get("/api/account").header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("integracion"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void unaClaveIncorrectaNoDaToken() throws Exception {
        mockMvc.perform(post("/api/authenticate").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"nadie\", \"password\": \"secreto123\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinTokenNoSeEntra() throws Exception {
        mockMvc.perform(get("/api/account")).andExpect(status().isUnauthorized());
    }

    @Test
    void unTokenFirmadoConOtraClaveSeRechaza() throws Exception {
        byte[] otraClave = new byte[64];
        new SecureRandom().nextBytes(otraClave);
        JwtTokenIssuer falsificador = new JwtTokenIssuer(
                new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(otraClave, "HmacSHA512"))),
                Duration.ofDays(1), Duration.ofDays(1));
        String tokenAjeno = falsificador.issue(
                User.builder().login("integracion").authorities(Set.of(User.ROLE_USER)).build(), false);

        mockMvc.perform(get("/api/account").header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenAjeno))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void elHealthEsPublico() throws Exception {
        mockMvc.perform(get("/actuator/health")).andExpect(status().isOk());
    }
}
