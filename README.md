## AI Interprise Intellegent Business  | S07-26-Team-26


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
