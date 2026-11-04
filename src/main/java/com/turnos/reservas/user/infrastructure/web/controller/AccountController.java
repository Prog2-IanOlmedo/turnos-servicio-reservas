package com.turnos.reservas.user.infrastructure.web.controller;

import java.security.Principal;

import com.turnos.reservas.user.application.service.UserService;
import com.turnos.reservas.user.infrastructure.web.dto.AccountResponse;
import com.turnos.reservas.user.infrastructure.web.dto.JwtTokenResponse;
import com.turnos.reservas.user.infrastructure.web.dto.LoginRequest;
import com.turnos.reservas.user.infrastructure.web.dto.RegisterRequest;
import com.turnos.reservas.user.infrastructure.web.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Rutas compatibles con JHipster: /api/register, /api/authenticate y /api/account.
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest body) {
        userService.register(userDtoMapper.toRegistration(body));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // El token va en el body (id_token) y también en el header, como en JHipster.
    @PostMapping("/authenticate")
    public ResponseEntity<JwtTokenResponse> authenticate(@Valid @RequestBody LoginRequest body) {
        String token = userService.authenticate(body.getUsername(), body.getPassword(), Boolean.TRUE.equals(body.getRememberMe()));
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(new JwtTokenResponse(token));
    }

    // El usuario sale del JWT validado, nunca de un parámetro.
    @GetMapping("/account")
    public ResponseEntity<AccountResponse> getAccount(Principal principal) {
        return ResponseEntity.ok(userDtoMapper.toAccountResponse(userService.getAccount(principal.getName())));
    }
}
