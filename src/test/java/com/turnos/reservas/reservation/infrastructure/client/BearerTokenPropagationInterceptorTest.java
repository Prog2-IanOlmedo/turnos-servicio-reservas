package com.turnos.reservas.reservation.infrastructure.client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class BearerTokenPropagationInterceptorTest {

    private MockRestServiceServer server;
    private RestClient restClient;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://catalogo")
                .requestInterceptor(new BearerTokenPropagationInterceptor());
        server = MockRestServiceServer.bindTo(builder).build();
        restClient = builder.build();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void reenviaElTokenDelUsuario() {
        Jwt jwt = Jwt.withTokenValue("token-del-usuario").header("alg", "HS512").subject("ian").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        server.expect(requestTo("http://catalogo/x"))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer token-del-usuario"))
                .andRespond(withSuccess());

        restClient.get().uri("/x").retrieve().toBodilessEntity();
        server.verify();
    }

    @Test
    void sinUsuarioNoAgregaNada() {
        server.expect(requestTo("http://catalogo/x"))
                .andExpect(headerDoesNotExist(HttpHeaders.AUTHORIZATION))
                .andRespond(withSuccess());

        restClient.get().uri("/x").retrieve().toBodilessEntity();
        server.verify();
    }
}
