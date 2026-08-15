## AI Interprise Intellegent Business  | Gosht Load Server
**Identificador de Eficiencia de Data Centers**

Ghost Load es una aplicación empresarial para operadores de data centers que ayuda a identificar capacidad no productiva, evaluar madurez operativa y generar reportes PDF personalizados. El proyecto está diseñado como un monolito modular con una arquitectura hexagonal que separa el negocio de las tecnologías externas.

![Frontend Evaluación](docs/images/Banner_Presentacion.png)

![Frontend Evaluación](docs/images/Resumen_Ejecutivo.png)
![Frontend Evaluación](docs/images/Banner_Equipo.png)



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

# Frontend - Benchmark

Vista de la interfaz correspondiente al módulo de Benchmark.

![Frontend Benchmark](docs/images/Front_End_Benchmark.png)

---

# Frontend - Evaluación

Vista de la interfaz correspondiente al módulo de Evaluación.

![Frontend Evaluación](docs/images/Front_End_Evaluación.png)

---

# Descarga de Reporte

La siguiente imagen muestra la funcionalidad de descarga de reportes disponible en la aplicación.

![Descarga de Reporte](docs/images/Descarga_Reporte.png)

<p align="center">
  <a href="docs/README.md">
    <img src="https://img.shields.io/badge/📚_Documentación-0F172A?style=for-the-badge&logo=readthedocs&logoColor=38BDF8">
  </a>
  <a href="docs/README.md">
    <img src="https://img.shields.io/badge/🏛️_Arquitectura-1E1B4B?style=for-the-badge&logo=github&logoColor=A78BFA">
  </a>
  <a href="docs/README.md">
    <img src="https://img.shields.io/badge/🚀_Roadmap-111827?style=for-the-badge&logo=rocket&logoColor=22D3EE">
  </a>
</p>

# Arquitectura

La siguiente imagen muestra la arquitectura general del sistema.

![Arquitectura](docs/images/Arquitectura_23-7.png)
