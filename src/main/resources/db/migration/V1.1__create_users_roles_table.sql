CREATE TABLE "users"
(
    "id"         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "public_id"  UUID NULL DEFAULT gen_random_uuid() UNIQUE,
    "role"       BIGINT                              NOT NULL,
    "email"      VARCHAR(255)                        NOT NULL,
    "password"   VARCHAR(255)                        NOT NULL,
    "created_at" TIMESTAMPTZ                         NOT NULL DEFAULT now(),
    "updated_at" TIMESTAMPTZ                         NOT NULL DEFAULT now()
);
ALTER TABLE
    "users"
    ADD PRIMARY KEY ("id");
ALTER TABLE
    "users"
    ADD CONSTRAINT "users_public_id_unique" UNIQUE ("public_id");
CREATE TABLE "user_role"
(
    "user_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL
);
ALTER TABLE
    "user_role"
    ADD PRIMARY KEY ("user_id", "role_id");
CREATE TABLE "roles"
(
    "id"          BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    "name"        VARCHAR(255)                        NOT NULL,
    "description" VARCHAR(255)                        NOT NULL,
    "created_at"  TIMESTAMPTZ                         NOT NULL DEFAULT now(),
    "updated_at"  TIMESTAMPTZ                         NOT NULL DEFAULT now()
);
ALTER TABLE
    "roles"
    ADD PRIMARY KEY ("id");
ALTER TABLE
    "user_role"
    ADD CONSTRAINT "user_role_role_id_foreign" FOREIGN KEY ("role_id") REFERENCES "roles" ("id");
ALTER TABLE
    "user_role"
    ADD CONSTRAINT "user_role_user_id_foreign" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

CREATE
OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at
= now();
RETURN NEW;
END;
$$
LANGUAGE plpgsql;

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE
    ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trigger_roles_updated_at
    BEFORE UPDATE
    ON roles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
