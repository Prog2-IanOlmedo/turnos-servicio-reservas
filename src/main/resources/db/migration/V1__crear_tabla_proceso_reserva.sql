-- Proceso de reserva local. El id es propio; reservation_process_id y
-- hold_id los asigna la cátedra cuando se crea el hold.
-- Largos tomados del contrato: externalPatientId hasta 100, motivo hasta 500.

CREATE TABLE reservation_process (
    id                     BIGSERIAL    PRIMARY KEY,
    reservation_process_id VARCHAR(100) UNIQUE,
    hold_id                VARCHAR(100),
    external_patient_id    VARCHAR(100) NOT NULL,
    professional_id        BIGINT       NOT NULL,
    appointment_date       DATE         NOT NULL,
    start_time             TIME         NOT NULL,
    status                 VARCHAR(20)  NOT NULL
        CHECK (status IN ('HELD','WAITING_FOR_PHONE','CONFIRMED','CANCELLED','EXPIRED','INVALID')),
    expires_at             TIMESTAMPTZ,
    reservation_id         BIGINT,
    status_reason          VARCHAR(500),
    created_at             TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Las reservas de un usuario se van a consultar seguido.
CREATE INDEX idx_reservation_process_patient ON reservation_process (external_patient_id);
