SET search_path TO ${schema};

DO $$
    BEGIN
        CREATE USER ${username} PASSWORD '${password}';
    EXCEPTION WHEN DUPLICATE_OBJECT THEN
        RAISE NOTICE 'not creating already-existing user';
    END
$$;
GRANT USAGE ON SCHEMA ${schema} TO ${username};

-- data tables
CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(128) NOT NULL,
    password VARCHAR(512) NOT NULL
);
CREATE INDEX users_username_idx ON users (username);
ALTER TABLE users ADD CONSTRAINT users_unique_username_idx UNIQUE (username);
GRANT SELECT, INSERT, UPDATE, DELETE ON users TO ${username};
GRANT USAGE ON users_id_seq TO ${username};

CREATE TABLE applications
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);
CREATE INDEX applications_name_idx ON applications (name);
ALTER TABLE applications ADD CONSTRAINT applications_unique_name_idx UNIQUE (name);
GRANT SELECT, INSERT, UPDATE, DELETE ON applications TO ${username};
GRANT USAGE ON applications_id_seq TO ${username};

CREATE TABLE roles
(
    id             BIGSERIAL PRIMARY KEY,
    application_id BIGINT      NOT NULL REFERENCES applications ON DELETE CASCADE,
    name           VARCHAR(64) NOT NULL
);
CREATE INDEX roles_app_idx ON roles (application_id);
CREATE INDEX roles_name_idx ON roles (application_id, name);
ALTER TABLE roles ADD CONSTRAINT roles_unique_name_idx UNIQUE (application_id, name);
GRANT SELECT, INSERT, UPDATE, DELETE ON roles TO ${username};
GRANT USAGE ON roles_id_seq TO ${username};

CREATE TABLE groups
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);
CREATE INDEX groups_name_idx ON groups (name);
ALTER TABLE groups ADD CONSTRAINT groups_unique_name_idx UNIQUE (name);
GRANT SELECT, INSERT, UPDATE, DELETE ON groups TO ${username};
GRANT USAGE ON groups_id_seq TO ${username};

-- relation tables
CREATE TABLE user_roles
(
    user_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles ON DELETE CASCADE
);
CREATE INDEX user_roles_user_id_idx ON user_roles (user_id);
CREATE INDEX user_roles_role_id_idx ON user_roles (role_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON user_roles TO ${username};

CREATE TABLE group_roles
(
    group_id BIGINT NOT NULL REFERENCES groups ON DELETE CASCADE,
    role_id  BIGINT NOT NULL REFERENCES roles ON DELETE CASCADE
);
CREATE INDEX group_roles_group_id_idx ON group_roles (group_id);
CREATE INDEX group_roles_role_id_idx ON group_roles (role_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON group_roles TO ${username};

CREATE TABLE user_groups
(
    user_id  BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
    group_id BIGINT NOT NULL REFERENCES groups ON DELETE CASCADE
);
CREATE INDEX user_groups_user_id_idx ON user_groups (user_id);
CREATE INDEX user_groups_group_id_idx ON user_groups (group_id);
GRANT SELECT, INSERT, UPDATE, DELETE ON user_groups TO ${username};

-- add initial roles
INSERT INTO applications (name) VALUES ('user-error');
INSERT INTO roles (application_id, name) VALUES ((select id from applications), 'users-user');
INSERT INTO roles (application_id, name) VALUES ((select id from applications), 'users-admin');
INSERT INTO groups (name) VALUES ('user-error-admin');
INSERT INTO group_roles (group_id, role_id) SELECT groups.id, roles.id FROM groups, roles WHERE groups.name = 'user-error-admin';

-- add initial user
INSERT INTO users (username, password) VALUES ('admin', '$2a$10$BOTzy5.IGWm2Y./TTQ9jm.nDwGhyP0aryzBJGK9ODD96pUEAVYZu.');
INSERT INTO user_groups (user_id, group_id) SELECT users.id, groups.id FROM users, groups WHERE username = 'admin';
