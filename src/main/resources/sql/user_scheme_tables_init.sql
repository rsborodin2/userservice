CREATE SCHEMA IF NOT EXISTS users_scheme
    AUTHORIZATION study;
	
-- Table: users_scheme.users

-- DROP TABLE IF EXISTS users_scheme.users;

CREATE TABLE IF NOT EXISTS users_scheme.users
(
    id uuid NOT NULL,
    surname character varying COLLATE pg_catalog."default" NOT NULL,
    firstname character varying COLLATE pg_catalog."default" NOT NULL,
    secondname character varying COLLATE pg_catalog."default",
    birth date NOT NULL,
    gender character varying COLLATE pg_catalog."default" NOT NULL,
    email character varying COLLATE pg_catalog."default" NOT NULL,
    phone character varying COLLATE pg_catalog."default",
    deleted boolean,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS users_scheme.users
    OWNER to study;


CREATE TABLE IF NOT EXISTS users_scheme.follows
(
    follower_user_id uuid NOT NULL,
    following_user_id uuid NOT NULL,
    created_at date NOT NULL,
    CONSTRAINT follower_user_id_following_user_id PRIMARY KEY (follower_user_id, following_user_id),
    CONSTRAINT follower_user_id FOREIGN KEY (follower_user_id)
        REFERENCES users_scheme.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT following_user_id FOREIGN KEY (following_user_id)
        REFERENCES users_scheme.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
ALTER TABLE IF EXISTS users_scheme.follows
    OWNER to study;