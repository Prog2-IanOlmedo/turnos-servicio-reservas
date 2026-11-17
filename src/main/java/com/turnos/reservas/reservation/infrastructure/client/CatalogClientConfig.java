package com.turnos.reservas.reservation.infrastructure.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

// Cliente HTTP hacia nuestro catálogo. A diferencia de la cátedra, acá no va el JWT
// técnico: va el del usuario, que lo agrega el interceptor.
@Configuration
public class CatalogClientConfig {

    @Bean
    RestClient catalogRestClient(
            @Value("${catalogo.api.url}") String url,
            @Value("${catalogo.api.connect-timeout}") Duration connectTimeout,
            @Value("${catalogo.api.read-timeout}") Duration readTimeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);
        return RestClient.builder()
                .baseUrl(url)
                .requestFactory(requestFactory)
                .requestInterceptor(new BearerTokenPropagationInterceptor())
                .build();
    }
}
