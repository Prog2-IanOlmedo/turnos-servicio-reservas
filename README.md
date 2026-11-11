<!-- calidad:inicio -->
![Calidad](https://img.shields.io/badge/Calidad-76%2F100-green) ![Aprobar](https://img.shields.io/badge/Aprobar-no-red) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Build](https://img.shields.io/badge/Build-OK-green) ![Duplicación](https://img.shields.io/badge/Duplicaci%C3%B3n-0.0%25-green) ![%POO](https://img.shields.io/badge/%25POO-70%25-yellow) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![D1](https://img.shields.io/badge/D1-3-red) ![D2](https://img.shields.io/badge/D2-100-green) ![D4](https://img.shields.io/badge/D4-80-green) ![D5](https://img.shields.io/badge/D5-100-green) ![D6](https://img.shields.io/badge/D6-100-green) ![D7](https://img.shields.io/badge/D7-100-green)

**Calidad (heurístico, 7 dimensiones):** índice **76/100** · build **OK** · aprobar **no (7/11)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->

## Levantar con Docker

Necesita un `.env` en la raíz (copiar `.env.example` y completar los valores).

```bash
docker compose up -d --build
```

- La API queda en `http://localhost:8082` (se cambia con `RESERVAS_APP_PORT`).
- PostgreSQL solo escucha en `127.0.0.1:5434`.
- El `.env` no se copia a la imagen: compose pasa los valores como variables de entorno.
