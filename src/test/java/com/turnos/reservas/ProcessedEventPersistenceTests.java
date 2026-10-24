package com.turnos.reservas;

import com.turnos.reservas.reservation.domain.ports.out.ProcessedEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ProcessedEventPersistenceTests {

    @Autowired
    private ProcessedEventRepository repository;

    @Test
    void recuerdaLosEventosProcesados() {
        assertThat(repository.isProcessed("confirmation-event-7d10")).isFalse();

        repository.markProcessed("confirmation-event-7d10");

        assertThat(repository.isProcessed("confirmation-event-7d10")).isTrue();
    }

    @Test
    void marcarDosVecesElMismoEventoNoFalla() {
        repository.markProcessed("process-event-7d12");
        repository.markProcessed("process-event-7d12");

        assertThat(repository.isProcessed("process-event-7d12")).isTrue();
    }
}
