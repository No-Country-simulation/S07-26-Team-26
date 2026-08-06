# Design — Administration

## Architecture
Bounded Context `administration` dentro de la arquitectura DDD + Hexagonal.
Orientado a consultas y acceso administrativo. No posee agregados complejos.

## Domain Model

### Entities
- `AdminUser` — Aggregate Root
  - id, email, password (hashed), name, role (ADMIN)

### Value Objects
- `AdminId`, `Email`, `HashedPassword`, `Role`

## Ports

### In (Use Cases)
- `AuthenticateAdminUseCase` — login(email, password) → JWT
- `GetDashboardSummaryQuery` — KPIs globales agregados
- `GetRecentResponsesQuery` — últimas respuestas del benchmark

### Out (Infrastructure)
- `LoadAdminUserPort`
- `PasswordEncoderPort`
- `JwtProviderPort`

## Adapters

### In
- `AdminAuthController` — POST /api/v1/admin/auth/login
- *(futuro)* DashboardController — GET /api/v1/admin/dashboard/summary

### Out
- `AdminUserPersistenceAdapter` — PostgreSQL
- `JwtProvider` — HMAC-SHA256 JWT
- `BCryptPasswordAdapter` — bcrypt

## API Endpoints
- POST /api/v1/admin/auth/login
- *(futuro)* GET /api/v1/admin/dashboard/summary

## Database
Tablas:
- `admin_users` (V1.1) + seed (V1.2)

## Security
- Admin: Bearer JWT en header Authorization
- Spring Security FilterChain valida en cada request
- Dominio no conoce headers HTTP
