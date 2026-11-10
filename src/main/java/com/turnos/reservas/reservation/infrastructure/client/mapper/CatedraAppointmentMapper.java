package com.turnos.reservas.reservation.infrastructure.client.mapper;

import java.util.Arrays;
import java.util.List;

import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.AppointmentStatus;
import com.turnos.reservas.reservation.domain.model.Hold;
import com.turnos.reservas.reservation.domain.model.Occupancy;
import com.turnos.reservas.reservation.domain.model.OccupancyStatus;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraAppointmentDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraHoldResponseDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraOccupanciesDto;
import com.turnos.reservas.reservation.infrastructure.client.dto.CatedraOccupancyDto;
import org.springframework.stereotype.Component;

@Component
public class CatedraAppointmentMapper {

    // Una lista vacía es una respuesta válida (sección 8).
    public List<Occupancy> toOccupancies(CatedraOccupanciesDto dto) {
        if (dto == null || dto.occupancies() == null) {
            return List.of();
        }
        return dto.occupancies().stream().map(this::toOccupancy).toList();
    }

    public Hold toHold(CatedraHoldResponseDto dto) {
        return new Hold(dto.holdId(), dto.reservationProcessId(), dto.expiresAt());
    }

    private Occupancy toOccupancy(CatedraOccupancyDto dto) {
        return new Occupancy(dto.date(), dto.startTime(),
                OccupancyStatus.valueOf(dto.status()), dto.expiresAt());
    }

    // Un estado que no conozco se saltea: no puedo decidir nada con él.
    public List<Appointment> toAppointments(CatedraAppointmentDto[] dtos) {
        if (dtos == null) {
            return List.of();
        }
        return Arrays.stream(dtos)
                .flatMap(dto -> AppointmentStatus.parse(dto.status()).stream()
                        .map(status -> new Appointment(dto.reservationProcessId(), dto.reservationId(), status)))
                .toList();
    }
}
