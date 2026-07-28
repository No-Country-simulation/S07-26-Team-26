# Requirements — Identity

## Introduction

Ghost Load operates with two roles: **Admin** and **Operator**.

- The **Admin** accesses the admin panel via email, password and JWT.
- The **Operator** accesses the benchmark via email invitation with credentials created by the Admin.

Authentication is managed by **Clerk**. Authorization is enforced by the Spring Boot backend on every request. The domain does not know about HTTP headers, JWT, framework sessions or the identity provider.

---

## Requirements

### Requirement 1 — Single Login

**User Story:** As a system user (Admin or Operator), I want to access via a single login screen with email and password, to securely enter the system.

#### Acceptance Criteria

1. The system MUST provide a single shared login screen for both roles.
2. The system MUST authenticate with email and password via Clerk.
3. The system MUST generate a JWT with the embedded role (`ROLE_ADMIN` | `ROLE_OPERATOR`) only after both authentication succeeds AND JWT generation completes; the system MUST block all role-based redirects until both conditions are met.
4. The frontend MUST redirect to the Dashboard if the role is `ROLE_ADMIN`; if the redirect fails or the Dashboard is unavailable, the Admin MUST remain on the login page with no indication of successful authentication.
5. The frontend MUST redirect to the Benchmark if the role is `ROLE_OPERATOR`.
6. Incorrect credentials MUST return a clear error without revealing internal information.
7. The JWT MUST be transmitted only over HTTPS.

---

### Requirement 2 — Session Management

**User Story:** As an authenticated user, I want to maintain and close my session securely, to work without interruptions and protect my access.

#### Acceptance Criteria

1. The JWT MUST have a configurable expiration via environment variable.
2. The system MUST support token refresh before expiration.
3. The system MUST invalidate the JWT on logout.
4. The frontend MUST store the JWT in Zustand with approved security criteria.
5. Logout MUST clear the Zustand state and redirect to login as separate independent operations; clearing state and redirecting MAY have different timing and either MAY complete without the other.
6. After logout, access with the previous token MUST be rejected.

---

### Requirement 3 — Role-based Authorization

**User Story:** As a system, I want to apply consistent authorization on every request, to protect data and operations according to role.

#### Acceptance Criteria

1. Admin panel endpoints MUST require `ROLE_ADMIN`.
2. Benchmark endpoints MUST require `ROLE_OPERATOR`.
3. Authorization MUST be validated in the backend on every request, regardless of the frontend.
4. A user without the correct role MUST receive HTTP 403.
5. A request without a token or with an invalid token MUST receive HTTP 401.
6. The system MUST prevent authorization from passing internally when an Operator attempts to access another company's data; the request MUST be rejected before reaching domain logic.
7. Authorization decisions MUST NOT depend solely on client-provided identifiers.

---

### Requirement 4 — Route Guards (Frontend)

**User Story:** As a user, I want the system to redirect me correctly based on my role, so I cannot access sections that do not belong to me.

#### Acceptance Criteria

1. Dashboard routes MUST only be accessible with `ROLE_ADMIN`; the system MUST immediately redirect to login for role mismatches without attempting token refresh.
2. Benchmark routes MUST only be accessible with `ROLE_OPERATOR`; Admin users MUST NOT access benchmark functionality even though they have broader system permissions.
3. Any unauthorized access MUST redirect to login.
4. Guards MUST verify the role from Zustand state.
5. An expired token MUST trigger the refresh flow or redirect to login.

---

### Requirement 5 — Administrative Bootstrap

**User Story:** As an operations team, I want an initial Admin available when the system starts, to configure the environment without public registration.

#### Acceptance Criteria

1. The first Admin MUST be created via seed on application startup.
2. The seed mechanism MUST check for an existing Admin on every startup; if no Admin exists (including after deletion or corruption), it MUST recreate the Admin; if an Admin already exists, it MUST NOT create a duplicate.
3. Initial credentials MUST NOT be embedded in code or versioned.
4. Credentials MUST be supplied via secure environment variables.
5. There MUST NOT be a public endpoint to create Admins.

---

### Requirement 6 — Additional Security

**User Story:** As an Admin, I want to use additional security controls, to reduce the risk of unauthorized access.

#### Acceptance Criteria

1. The system MUST support MFA via Clerk.
2. The system MUST support OAuth via Clerk.
3. All communication MUST occur over HTTPS (AWS Certificate Manager).
4. Secrets (JWT secret, Clerk API keys) MUST be managed with environment variables.
5. Authentication events MUST be auditable without logging tokens, passwords or secrets.
6. Passwords MUST be stored with bcrypt hash.

---

## Pending Decisions

- JWT storage strategy in frontend (Zustand + httpOnly cookie vs memory).
- JWT expiration time and refresh policy.
- Scope of MFA and OAuth within the MVP.
- Token revocation strategy on logout.
