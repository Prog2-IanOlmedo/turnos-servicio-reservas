package com.turnos.reservas;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ReservationPersistenceTests {

    @Autowired
    private ReservationProcessRepository repository;

    // Datos tomados del ejemplo de hold del contrato.
    private ReservationProcess nuevoHold(String processId) {
        return ReservationProcess.builder()
                .reservationProcessId(processId)
                .holdId("hold-6c0c2f41")
                .externalPatientId("user-42")
                .professionalId(15L)
                .date(LocalDate.parse("2026-09-14"))
                .startTime(LocalTime.of(9, 30))
                .status(ReservationStatus.HELD)
                .expiresAt(Instant.parse("2026-09-10T14:44:50Z"))
                .build();
    }

    @Test
    void guardaElProcesoYLoEncuentraPorLaClaveDeLaCatedra() {
        ReservationProcess guardado = repository.save(nuevoHold("process-a92b47d0"));

        assertThat(guardado.getId()).isNotNull();
        ReservationProcess encontrado = repository.findByReservationProcessId("process-a92b47d0").orElseThrow();
        assertThat(encontrado.getId()).isEqualTo(guardado.getId());
        assertThat(encontrado.getDate()).isEqualTo(LocalDate.parse("2026-09-14"));
        assertThat(encontrado.getStatus()).isEqualTo(ReservationStatus.HELD);
    }

    @Test
    void unaTransicionDelDominioQuedaGuardada() {
        ReservationProcess proceso = repository.save(nuevoHold("process-b1"));

        proceso.markWaitingForPhone(Instant.parse("2026-09-10T14:44:50Z"));
        proceso.confirm(8251L);
        repository.save(proceso);

        ReservationProcess recargado = repository.findById(proceso.getId()).orElseThrow();
        assertThat(recargado.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(recargado.getReservationId()).isEqualTo(8251L);
    }
}
