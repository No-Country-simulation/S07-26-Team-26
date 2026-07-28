# Tasks — Identity

## Status Legend
- [ ] Pendiente
- [x] Completado
- [-] En progreso

---

## Backend

- [ ] Crear entidad `User` con JPA (REQ-1, REQ-3)
- [ ] Crear tabla `users` en PostgreSQL (migration) (REQ-1, REQ-3)
- [ ] Implementar hash bcrypt para contraseñas (REQ-6.6)
- [ ] Implementar `UserRepository` (JPA Adapter) (REQ-1, REQ-5)
- [ ] Implementar `AuthUseCase` — login, logout, refresh, me (REQ-1, REQ-2)
- [ ] Configurar Spring Security con JWT Filter (REQ-3)
- [ ] Implementar generación y validación de JWT — bloquear redirects hasta que ambos completen (REQ-1.3)
- [ ] Implementar `ClerkAuthAdapter` para MFA y OAuth (REQ-6.1, REQ-6.2)
- [ ] Crear seed del primer Admin en startup — recrear si fue eliminado, no duplicar si existe (REQ-5.2)
- [ ] Implementar `AuthController` con endpoints REST (REQ-1, REQ-2)
- [ ] Agregar validación de roles en cada endpoint — HTTP 401 sin token, HTTP 403 con rol incorrecto (REQ-3.4, REQ-3.5)
- [ ] Implementar rechazo de acceso a datos de otra empresa antes del dominio (REQ-3.6)

### Tests Backend (REQ coverage)
- [ ] Test: login exitoso genera JWT con rol correcto (REQ-1.3)
- [ ] Test: credenciales incorrectas devuelven error sin revelar info interna (REQ-1.6)
- [ ] Test: endpoint sin token devuelve HTTP 401 (REQ-3.5)
- [ ] Test: endpoint con rol incorrecto devuelve HTTP 403 (REQ-3.4)
- [ ] Test: Admin no puede acceder a endpoints de ROLE_OPERATOR (REQ-4.2)
- [ ] Test: Operator no puede acceder a datos de otra empresa — rechazado antes del dominio (REQ-3.6)
- [ ] Test: logout invalida el JWT — request posterior devuelve 401 (REQ-2.6)
- [ ] Test: seed es idempotente — no crea duplicado si Admin existe (REQ-5.2)
- [ ] Test: seed recrea Admin si fue eliminado (REQ-5.2)
- [ ] Test: no existe endpoint público para crear Admins (REQ-5.5)
- [ ] Test: token expirado devuelve 401 (REQ-2.1)

## Frontend

- [ ] Crear pantalla de Login (email + password) (REQ-1.1)
- [ ] Integrar Clerk en el flujo de autenticación (REQ-1.2, REQ-6.1, REQ-6.2)
- [ ] Implementar almacenamiento de JWT en Zustand (REQ-2.4)
- [ ] Implementar guards de ruta por rol en Next.js — redirect inmediato sin intentar refresh en role mismatch (REQ-4.1)
- [ ] Implementar redirección post-login: Dashboard para Admin, Benchmark para Operator (REQ-1.4, REQ-1.5)
- [ ] Si redirect al Dashboard falla, mantener en login sin indicar éxito (REQ-1.4)
- [ ] Implementar logout — limpiar Zustand y redirect como operaciones independientes (REQ-2.5)
- [ ] Implementar refresh automático de token antes de expiración (REQ-2.2)
- [ ] Admin no puede navegar a rutas de Benchmark (REQ-4.2)

## DevOps / Config

- [ ] Configurar variables de entorno para JWT secret (REQ-5.3, REQ-5.4)
- [ ] Configurar Clerk en el proyecto (API keys) (REQ-6.1, REQ-6.2)
- [ ] Configurar CORS para el dominio del frontend (REQ-3.3)
- [ ] Verificar que toda comunicación es sobre HTTPS (REQ-6.3)
