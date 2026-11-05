-- eventId del pedido de teléfono (AdditionalInformationRequested). La respuesta
-- lo tiene que mandar como requestEventId (contrato, sección 15.5).
ALTER TABLE reservation_process ADD COLUMN phone_request_event_id VARCHAR(100);
