package com.turnos.reservas.shared.web;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;

// Arma el cuerpo problem+json con el "code" estable que pide el contrato.
public final class ApiProblem {

    private ApiProblem() {
    }

    public static ProblemDetail of(HttpStatusCode status, String code, String detail, String path) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setProperty("code", code);
        if (path != null) {
            problem.setInstance(URI.create(path));
        }
        return problem;
    }
}
