CREATE TABLE admin_users (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(30) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_admin_users_email UNIQUE (email),
    CONSTRAINT ck_admin_users_role CHECK (role IN ('ADMIN'))
);
