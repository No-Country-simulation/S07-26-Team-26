## Diccionario de servicios Docker

| Componente | Servicio Docker | Contenedor | Tecnología / imagen | Puerto local | Puerto interno | Descripción |
| --- | --- | --- | --- | --- | --- | --- |
| Base de datos | `postgres` | `ghost-load-db` | `postgres:16-alpine` | `5432` | `5432` | Almacena los datos de la aplicación |
| Backend | `backend` | `backend` | Spring Boot / Dockerfile en `./backend` | `8080` | `8080` | API y lógica de negocio |
| Frontend | `frontend` | `frontend` | Next.js / Dockerfile en `./frontend` | `3001` | `3000` | Interfaz web de la aplicación |

### PostgreSQL

| Propiedad | Valor |
| --- | --- |
| Host dentro de Docker | `postgres` |
| Puerto | `5432` |
| Base de datos | `ghost_load` |
| Usuario local | `postgres` |
| Volumen persistente | `postgres_data` |
| Ruta del volumen | `/var/lib/postgresql/data` |
| URL utilizada por el backend | `jdbc:postgresql://postgres:5432/ghost_load` |

### Comunicación entre servicios

- El frontend se comunica con el backend mediante `http://backend:8080`.
- El backend se comunica con PostgreSQL mediante `postgres:5432`.
- PostgreSQL debe estar saludable antes de iniciar el backend.
- El backend debe iniciarse antes que el frontend.

### Acceso desde la computadora

| Aplicación | Dirección |
| --- | --- |
| Frontend | `http://localhost:3001` |
| Backend | `http://localhost:8080` |
| PostgreSQL | `localhost:5432` |

> Los nombres `postgres`, `backend` y `frontend` funcionan como nombres de host dentro de la red interna de Docker Compose.
