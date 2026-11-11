package com.turnos.reservas.reservation.application.usecases;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.turnos.reservas.reservation.domain.model.Appointment;
import com.turnos.reservas.reservation.domain.model.ReservationProcess;
import com.turnos.reservas.reservation.domain.ports.in.ReconcileOverdueProcessesUseCase;
import com.turnos.reservas.reservation.domain.ports.out.AppointmentGateway;
import com.turnos.reservas.reservation.domain.ports.out.ReservationProcessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Antes de cerrar un proceso vencido le pregunto a la cátedra cómo quedó: un timeout no
// prueba que la operación haya fallado (contrato 18.4). Espero un minuto después de
// expiresAt para darle tiempo al evento de Kafka, que es el camino normal. Consulto una
// sola vez por paciente.
@Slf4j
@Component
@RequiredArgsConstructor
public class ReconcileOverdueProcessesUseCaseImpl implements ReconcileOverdueProcessesUseCase {

    static final Duration GRACE = Duration.ofMinutes(1);

    private final ReservationProcessRepository processRepository;
    private final AppointmentGateway appointmentGateway;

    @Override
    public int reconcile() {
        Instant cutoff = Instant.now().minus(GRACE);
        Map<String, List<ReservationProcess>> overdueByPatient = processRepository.findOpenProcesses().stream()
                .filter(process -> process.isOverdueAt(cutoff))
                .collect(Collectors.groupingBy(ReservationProcess::getExternalPatientId));
        int changed = 0;
        for (Map.Entry<String, List<ReservationProcess>> entry : overdueByPatient.entrySet()) {
            Map<String, Appointment> appointments = appointmentGateway.findAppointments(entry.getKey()).stream()
                    .collect(Collectors.toMap(Appointment::reservationProcessId, appointment -> appointment,
                            (first, second) -> first));
            for (ReservationProcess process : entry.getValue()) {
                if (process.reconcileWith(appointments.get(process.getReservationProcessId()), cutoff)) {
                    processRepository.save(process);
                    log.info("Proceso {} reconciliado con la cátedra: {}", process.getReservationProcessId(),
                            process.getStatus());
                    changed++;
                }
            }
        }
        return changed;
    }
}
