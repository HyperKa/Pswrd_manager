-- liquibase changeset admin:1
CREATE TABLE users (
        id UUID PRIMARY KEY,
        username VARCHAR(255) UNIQUE NOT NULL,
        salt BYTEA NOT NULL,
        auth_hash VARCHAR(255) NOT NULL,
        encrypted_vault TEXT NOT NULL,
        role VARCHAR(50) NOT NULL,
        created_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP NOT NULL,
        version BIGINT,
        last_login_at TIMESTAMP,
        locked_until TIMESTAMP,
        failed_attempts INTEGER DEFAULT 0
);

-- дефолтный админ
INSERT INTO users (id, username, salt, auth_hash, encrypted_vault, role, created_at, updated_at, version, failed_attempts)
VALUES (
           '550e8400-e29b-41d4-a716-446655440000',
           'admin',
           'YWRtaW5zYWx0',
           '$2a$10$.mfWD16N5q/ecju95RD9w.6pzNREC604X.F9L2qq02UPbPygERc8y',
           '{}',
           'ADMIN',
           NOW(), NOW(), 0, 0
       );