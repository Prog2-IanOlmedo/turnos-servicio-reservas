package com.turnos.reservas;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.model.ReservationStatus;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class OpenProcessesPersistenceTests {

    @Autowired
    private ReservationProcessRepository repository;

    private void guardar(String id, ReservationStatus status) {
        repository.save(ReservationProcess.builder().reservationProcessId(id).externalPatientId("abiertos")
                .professionalId(202L).date(LocalDate.of(2026, 11, 16)).startTime(LocalTime.of(9, 0))
                .status(status).expiresAt(Instant.now()).build());
    }

    @Test
    void devuelveSoloLosProcesosQueSiguenAbiertos() {
        guardar("abierto-held", ReservationStatus.HELD);
        guardar("abierto-telefono", ReservationStatus.WAITING_FOR_PHONE);
        guardar("cerrado-confirmado", ReservationStatus.CONFIRMED);
        guardar("cerrado-vencido", ReservationStatus.EXPIRED);

        assertThat(repository.findOpenProcesses())
                .extracting(ReservationProcess::getReservationProcessId)
                .contains("abierto-held", "abierto-telefono")
                .doesNotContain("cerrado-confirmado", "cerrado-vencido");
    }
}
