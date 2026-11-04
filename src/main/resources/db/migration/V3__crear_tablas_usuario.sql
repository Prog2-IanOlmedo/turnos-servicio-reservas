-- Usuarios finales de la app. Los campos siguen al usuario de JHipster para que
-- el registro sea compatible (enunciado, sección 3.2). "user" es palabra reservada
-- en PostgreSQL, por eso app_user. La clave se guarda solo como hash Argon2id.

CREATE TABLE app_user (
    id            BIGSERIAL    PRIMARY KEY,
    login         VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(50),
    last_name     VARCHAR(50),
    email         VARCHAR(254) NOT NULL UNIQUE,
    image_url     VARCHAR(256),
    activated     BOOLEAN      NOT NULL,
    lang_key      VARCHAR(10),
    created_date  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Roles de cada usuario. Al registrarse recibe solo ROLE_USER.
CREATE TABLE user_authority (
    user_id        BIGINT      NOT NULL REFERENCES app_user (id) ON DELETE CASCADE,
    authority_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, authority_name)
);
