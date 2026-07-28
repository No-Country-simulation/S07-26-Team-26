# Design — Identity

## Architecture
Bounded Context dentro de la arquitectura DDD + Hexagonal.

## Domain Model

### Entities
- `User` — Aggregate Root
  - id: UUID
  - email: String
  - password: String (hashed)
  - name: String
  - role: Role (ROLE_ADMIN | ROLE_OPERATOR)
  - status: UserStatus (ACTIVE | SUSPENDED)
  - companyId: UUID (nullable, solo ROLE_OPERATOR)

### Value Objects
- `Email` — validación de formato
- `Password` — hash bcrypt
- `Token` — JWT con rol embebido y expiración

## Ports

### In (Use Cases)
- `AuthUseCase`
  - login(email, password) → TokenResponse
  - logout(token) → void
  - refresh(token) → TokenResponse
  - me(token) → UserProfile

### Out (Infrastructure)
- `UserRepository` — persistencia de usuarios
- `SessionRepository` — gestión de tokens invalidados
- `ClerkAuthPort` — integración con Clerk (MFA, OAuth)

## Adapters

### In
- `AuthController` — REST endpoints `/auth/**`

### Out
- `UserJpaAdapter` — PostgreSQL via Spring Data JPA
- `ClerkAuthAdapter` — Clerk SDK

## API Endpoints
- POST /auth/login
- POST /auth/logout
- GET  /auth/me
- POST /auth/refresh

## Security Flow
```
Request → JWT Filter → Validate Token → Extract Role → Spring Security Context → Controller
```

## Database
Tabla: `users`
- Seed del primer Admin en startup de la aplicación
