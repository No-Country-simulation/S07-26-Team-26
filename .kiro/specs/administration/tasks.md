# Implementation Plan

## Overview

Plan de implementación para el módulo Administration: autenticación de administradores (JWT), dashboard de KPIs globales y listado de evaluaciones completadas. El backend sigue arquitectura DDD + Hexagonal. El frontend usa React con layout de sidebar.

## Tasks

### Backend — Dashboard

- [x] 1. Crear entidad `AdminUser` con JPA + tabla `admin_users` (V1.1)
  - Crear migración Flyway V1.1 con tabla `admin_users`
  - Crear entidad JPA `AdminUser` con campos id, email, password, name, role
  - **Requirement:** Requirement 1, AC-6

- [x] 2. Seed de admin inicial (V1.2)
  - Crear migración Flyway V1.2 que inserta el admin por defecto (bcrypt)
  - **Requirement:** Requirement 1, AC-6

- [x] 3. Implementar `AdminAuthController` — POST /api/v1/admin/auth/login
  - Definir `AdminLoginRequest` y `AdminLoginResponse`
  - Implementar el endpoint que delega a `AuthenticateAdminUseCase`
  - **Requirement:** Requirement 1, AC-1, AC-3

- [x] 4. Implementar `AuthenticateAdminService` — validar email/password
  - Cargar admin por email via `LoadAdminUserPort`
  - Verificar password con `PasswordEncoderPort`
  - Generar JWT via `JwtProviderPort`
  - **Requirement:** Requirement 1, AC-2, AC-3, AC-4

- [x] 5. Implementar `JwtProvider` — generación y validación de JWT
  - Generar tokens HMAC-SHA256 con adminId y rol embebidos
  - Validar tokens y extraer claims
  - **Requirement:** Requirement 1, AC-3

- [x] 6. Implementar `JwtAuthenticationFilter` — extraer token, set SecurityContext
  - Filtro que lee Authorization header, valida JWT y establece el contexto de seguridad
  - **Requirement:** Requirement 1, AC-5

- [x] 7. Implementar `SecurityConfig` — stateless, rutas protegidas
  - Configurar Spring Security sin sesión
  - Proteger /api/v1/admin/** con rol ADMIN
  - **Requirement:** Requirement 1, AC-5

- [x] 8. Implementar `BCryptPasswordAdapter` — hash de contraseñas
  - Adaptador que implementa `PasswordEncoderPort` usando BCrypt
  - **Requirement:** Requirement 1, AC-2

- [x] 9. Implementar `GetDashboardSummaryQuery` — KPIs globales
  - Definir el puerto de entrada `GetDashboardSummaryQuery` con parámetros de filtro por fecha
  - Implementar `GetDashboardSummaryService` que agrega: total de operadores, benchmarks completados, score promedio, PDFs generados
  - Implementar `LoadDashboardStatsPort` (puerto de salida) e implementación JPA que consulta las tablas correspondientes
  - **Requirement:** Requirement 2, AC-1, AC-2, AC-3

- [x] 10. Implementar endpoint GET /api/v1/admin/dashboard/summary
  - Crear `DashboardController` con el endpoint que recibe parámetros `from` y `to` (fecha)
  - Crear `DashboardSummaryResponse` DTO con los KPIs
  - Proteger el endpoint con JWT de rol ADMIN
  - **Requirement:** Requirement 2, AC-1, AC-3

- [x] 11. Implementar listado de evaluaciones completadas (RF-07.2)
  - Definir `GetRecentResponsesQuery` con filtros opcionales de paginación y fecha
  - Implementar servicio que recupera evaluaciones completadas con campos: empresa/operador, score, percentil, nivel de madurez, fecha
  - Implementar `LoadCompletedEvaluationsPort` e implementación JPA
  - **Requirement:** Requirement 3, AC-1, AC-2, AC-3

- [x] 12. Implementar endpoint GET /api/v1/admin/dashboard/recent-responses
  - Agregar endpoint en `DashboardController` con paginación
  - Crear `RecentResponsesPageResponse` DTO
  - Proteger con JWT de rol ADMIN
  - **Requirement:** Requirement 3, AC-3

### Backend — Tests

- [x] 13. Test: login exitoso genera JWT con rol ADMIN
  - **Requirement:** Requirement 1, AC-3

- [x] 14. Test: credenciales incorrectas devuelven 401
  - **Requirement:** Requirement 1, AC-4

- [x] 15. Test: endpoint sin token devuelve 401
  - **Requirement:** Requirement 1, AC-5

- [x] 16. Test: seed es idempotente
  - Verificar que ejecutar la migración V1.2 dos veces no duplica el admin ni lanza error
  - **Requirement:** Requirement 1, AC-6

### Frontend

- [x] 17. Crear pantalla de login admin
  - Formulario con campos email y contraseña
  - Llamada a POST /api/v1/admin/auth/login; guardar JWT en memoria/sessionStorage
  - Redirigir al dashboard en login exitoso; mostrar error en credenciales incorrectas
  - **Requirement:** Requirement 1, AC-1, AC-4

- [x] 18. Crear layout del dashboard con sidebar de navegación
  - Layout base con sidebar (links: Dashboard, Operators)
  - Header con nombre de usuario y botón de logout
  - Rutas protegidas que redirigen a login si no hay JWT válido
  - **Requirement:** Requirement 2

- [x] 19. Crear vista de KPIs globales
  - Componente que llama a GET /api/v1/admin/dashboard/summary
  - Mostrar tarjetas con: total operadores, benchmarks completados, score promedio, PDFs generados
  - Selector de rango de fechas que refresca los datos
  - **Requirement:** Requirement 2, AC-1, AC-2, AC-3

## Task Dependency Graph

```json
{
  "waves": [
    {
      "wave": 1,
      "tasks": ["9", "11", "16", "17"],
      "description": "Tareas independientes que pueden ejecutarse en paralelo"
    },
    {
      "wave": 2,
      "tasks": ["10", "12", "18"],
      "description": "Dependen de tareas de la ola 1"
    },
    {
      "wave": 3,
      "tasks": ["19"],
      "description": "Depende del endpoint de summary (10) y del layout (18)"
    }
  ],
  "dependencies": {
    "10": ["9"],
    "12": ["11"],
    "18": ["17"],
    "19": ["10", "18"]
  }
}
```

## Notes

- Las tareas 1–19 están completadas (MVP de Administration finalizado).
- Las tareas marcadas `[future]` (histogramas, evolución mensual, exportación CSV/PDF) están fuera del alcance del MVP y no se incluyen aquí.
- El frontend asume que existe un cliente HTTP configurado (axios o fetch wrapper) con interceptor para adjuntar el Bearer token.
- Los endpoints del dashboard requieren que existan datos en las tablas de operadores y evaluaciones (otros bounded contexts); los tests de integración deben usar fixtures.
