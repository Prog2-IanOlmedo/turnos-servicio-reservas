-- Eventos de Kafka ya aplicados. La entrega es "al menos una vez":
-- si el event_id ya está acá, el evento se ignora (contrato 18.1).
CREATE TABLE processed_event (
    event_id     VARCHAR(100) PRIMARY KEY,
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
