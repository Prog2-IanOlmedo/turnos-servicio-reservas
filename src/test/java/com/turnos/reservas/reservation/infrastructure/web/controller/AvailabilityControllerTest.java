package com.turnos.reservas.reservation.infrastructure.web.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.turnos.reservas.reservation.application.exception.CatalogUnavailableException;
import com.turnos.reservas.reservation.application.exception.ProfessionalNotFoundException;
import com.turnos.reservas.reservation.application.service.ReservationService;
import com.turnos.reservas.reservation.domain.model.Availability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AvailabilityControllerTest {

    private static final LocalDate MARTES = LocalDate.of(2026, 11, 17);

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AvailabilityController(reservationService)).build();
    }

    @Test
    void devuelveLosTurnosLibres() throws Exception {
        when(reservationService.getAvailability(202L, MARTES)).thenReturn(
                new Availability(202L, MARTES, List.of(LocalTime.of(11, 30), LocalTime.of(12, 0))));

        mockMvc.perform(get("/api/professionals/202/availability").param("date", "2026-11-17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.professionalId").value(202))
                .andExpect(jsonPath("$.date").value("2026-11-17"))
                .andExpect(jsonPath("$.slots[0]").value("11:30"))
                .andExpect(jsonPath("$.slots.length()").value(2));
    }

    @Test
    void profesionalInexistenteDa404() throws Exception {
        when(reservationService.getAvailability(999L, MARTES)).thenThrow(new ProfessionalNotFoundException(999L));

        mockMvc.perform(get("/api/professionals/999/availability").param("date", "2026-11-17"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROFESSIONAL_NOT_FOUND"));
    }

    @Test
    void catalogoCaidoDa503() throws Exception {
        when(reservationService.getAvailability(202L, MARTES))
                .thenThrow(new CatalogUnavailableException("sin respuesta", null));

        mockMvc.perform(get("/api/professionals/202/availability").param("date", "2026-11-17"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("CATALOG_UNAVAILABLE"));
    }
}
