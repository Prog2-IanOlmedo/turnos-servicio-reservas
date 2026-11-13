package com.turnos.reservas.reservation.infrastructure.client;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import com.turnos.reservas.reservation.application.exception.CatalogUnavailableException;
import com.turnos.reservas.reservation.application.exception.ProfessionalNotFoundException;
import com.turnos.reservas.reservation.domain.model.WeeklySlots;
import com.turnos.reservas.reservation.domain.ports.out.ProfessionalScheduleSource;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatalogScheduleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class CatalogScheduleClient implements ProfessionalScheduleSource {

    private final RestClient catalogRestClient;

    // Un 404 del catálogo es un profesional que no existe (o está deshabilitado).
    // Cualquier otra falla la informo como catálogo no disponible.
    @Override
    public List<WeeklySlots> findWeeklySlots(Long professionalId) {
        CatalogScheduleDto[] dtos;
        try {
            dtos = catalogRestClient.get()
                    .uri("/api/professionals/{id}/schedules", professionalId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CatalogScheduleDto[].class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProfessionalNotFoundException(professionalId);
        } catch (RestClientException e) {
            throw new CatalogUnavailableException("No se pudo consultar la agenda en el catálogo", e);
        }
        if (dtos == null) {
            return List.of();
        }
        return Arrays.stream(dtos)
                .map(dto -> new WeeklySlots(DayOfWeek.valueOf(dto.dayOfWeek()), dto.slots()))
                .toList();
    }
}
