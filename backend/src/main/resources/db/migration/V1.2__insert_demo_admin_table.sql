INSERT INTO admin_users (
    id,
    name,
    email,
    password_hash,
    role,
    active,
    created_at,
    updated_at
) VALUES (
    '8f744cf4-df09-4dc1-985a-a1bb27f7b25f',
    'Ghost Load Admin',
    'admin@ghostload.local',
    '$2a$12$x3FX7w3EcHqbLH5yJX4U3eshlxdegm3f8EjCEZ1tycpcF2Mc2xulu',
    'ADMIN',
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
