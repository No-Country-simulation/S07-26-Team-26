## AI Interprise Intellegent Business  | Gosht Load Server


<p align="center">
  <a href="https://youtu.be/ACqUvRgQqZY?si=gKL-bPkWQ4mJ9nM5">
    <img src="docs/images/youtube_kry_style.png" alt="YouTube" width="190">
  </a>
  &nbsp;&nbsp;&nbsp;&nbsp;
  <a href="https://github.com/No-Country-simulation/S07-26-Team-26/blob/main/docs/README.md">
    <img src="docs/images/next_steps_kry_style.png" alt="Next Steps" width="190">
  </a>
</p>

**Identificador de Eficiencia de Data Centers**


Ghost Load es una aplicación empresarial para operadores de data centers que ayuda a identificar capacidad no productiva, evaluar madurez operativa y generar reportes PDF personalizados. El proyecto está diseñado como un monolito modular con una arquitectura hexagonal que separa el negocio de las tecnologías externas.
<br>
<br>
![Frontend Evaluación](docs/images/Ficha_Presentacion.png)



docs/images/Banner_Presentacion.png

![Frontend Evaluación](docs/images/Banner_Presentacion.png)
<br>
<br>

## Flujo operativo

Ghost Load automatiza el recorrido completo del operador, desde la invitación hasta la generación del reporte final:

1. El administrador crea y envía una **campaña por email**.
2. El operador recibe la invitación e ingresa a la plataforma.
3. Completa la **calculadora de capacidad**.
4. Responde el **benchmark de madurez operativa**.
5. El sistema procesa los resultados y genera métricas e insights.
6. Finalmente, se crea un **reporte PDF personalizado** listo para descargar y compartir.
<br>
<br>


![Frontend Evaluación](docs/images/Diagram_Secuencia.png)
<br>
<br>




## Resumen ejecutivo

El dashboard ejecutivo de **Ghost Load** consolida los principales indicadores de eficiencia del data center en una única vista.

Permite visualizar la **capacidad total**, la **capacidad no productiva**, el nivel de **madurez operativa**, el **ahorro potencial** y la evolución de los principales KPIs.

Además, compara los resultados obtenidos con **benchmarks de la industria** y facilita la identificación de oportunidades de optimización para apoyar decisiones basadas en datos.
<br>
<br>

![Frontend Evaluación](docs/images/Resumen_Ejecutivo.png)
<br>
<br>


## El equipo detrás de Ghost Load

**Ghost Load** fue desarrollado por un equipo multidisciplinario que combina gestión de producto, arquitectura de software, desarrollo backend, frontend e infraestructura cloud.
El equipo trabaja de forma colaborativa para construir una solución **escalable, segura y orientada a datos**, integrando tecnologías modernas de desarrollo y servicios de **AWS Cloud**.

![Frontend Evaluación](docs/images/Banner_Equipo.png)

<br>
<br>


![Frontend Evaluación](docs/images/Arquitectura.png)


# Stack tecnológico

## Lenguajes

- **Java 21** — Backend
  - Aproximadamente **93.7%** del proyecto
  - Spring Boot 4.1.0

- **TypeScript** — Frontend
  - Aproximadamente **5.8%** del proyecto
  - React 19
  - Next.js 16.2.11

## Frameworks y runtime

### Backend

- **Spring Boot 4.1.0**
- **Java 21**
- **Maven**

### Frontend

- **Next.js 16.2.11**
- **React 19**
- **TypeScript**

## Tecnologías principales

- **Base de datos:** PostgreSQL 16
- **Autenticación:** JWT — JSON Web Tokens
- **Generación de PDF:** OpenHtmlToPDF
- **Procesamiento de CSV:** Apache Commons CSV
- **State Management:** Zustand
- **HTTP Client:** TanStack React Query
- **UI Components:** Lucide React
- **Estilos:** Tailwind CSS
- **Contenedores:** Docker
- **Cloud:** AWS

---

# Estructura del proyecto

```text
.
├── backend/                          # API REST con lógica de negocio
│   ├── src/main/java/com/ghostload/api/
│   │   ├── assessment/              # Evaluaciones y calculadora
│   │   ├── reporting/               # Generación de reportes PDF
│   │   ├── outreach/                # Campañas e invitaciones
│   │   ├── administration/          # Panel admin y login
│   │   ├── shared/                  # Código compartido
│   │   └── config/                  # Configuración Spring
│   ├── pom.xml                      # Dependencias Maven
│   ├── .env.example                 # Variables de entorno
│   └── Dockerfile
│
├── frontend/                         # Interfaz web con Next.js
│   ├── package.json
│   ├── Dockerfile
│   └── src/
│
├── docker-compose.yml               # Orquestación de servicios
├── README.md                        # Documentación principal
└── docs/
    ├── README.md
    ├── openapi.yaml                 # Contrato HTTP
    └── images/


---
```



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















# Ejecución con Docker

Todos los servicios se levantan desde el único `docker-compose.yml` ubicado en
la raíz del repositorio.

Primero crea el archivo local de variables y completa sus valores:

```powershell
Copy-Item backend/.env.example backend/.env
```

Después ejecuta, desde la raíz del repositorio:

```powershell
docker compose --env-file backend/.env up --build
```

No utilices solamente `docker compose up`, porque Docker Compose necesita las
variables definidas en `backend/.env`.

Cuando Docker informe que el backend está saludable, también puedes comprobarlo
manualmente sin autenticación:

```powershell
curl.exe http://localhost:8080/actuator/health
curl.exe http://localhost:8080/actuator/health/liveness
curl.exe http://localhost:8080/actuator/health/readiness
```

Los endpoints específicos devuelven `{"status":"UP"}` cuando el proceso está
vivo y el backend puede conectarse a PostgreSQL. El endpoint general también
enumera los grupos públicos `liveness` y `readiness`, pero no expone
credenciales ni detalles de los componentes internos.

Para detener todos los servicios:

```powershell
docker compose --env-file backend/.env down
```

---



