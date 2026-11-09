-- create_roles.sql
-- Ejecutar como superuser (postgres)

DO $$
BEGIN
    -- crear/actualizar ddl_user
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ddl_user') THEN
        CREATE ROLE ddl_user LOGIN PASSWORD 'TBMiJgwqDwbSSSIcHz7G0g==';
    ELSE
        ALTER ROLE ddl_user WITH LOGIN PASSWORD 'TBMiJgwqDwbSSSIcHz7G0g==';
    END IF;

    -- crear/actualizar app_user
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_user') THEN
        CREATE ROLE app_user LOGIN PASSWORD 'lxv4GqcNzKWI1T6rmmwYkw==';
    ELSE
        ALTER ROLE app_user WITH LOGIN PASSWORD 'lxv4GqcNzKWI1T6rmmwYkw==';
    END IF;

    -- crear/actualizar exporter
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'exporter') THEN
        CREATE ROLE exporter LOGIN PASSWORD 'HQrj4/tcZM3Koe8gmIW9fg==';
    ELSE
        ALTER ROLE exporter WITH LOGIN PASSWORD 'HQrj4/tcZM3Koe8gmIW9fg==';
    END IF;
END $$;

-- GRANTS a nivel DB: conectar a "Gym"
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_database WHERE datname = 'Gym') THEN
        EXECUTE format('GRANT CONNECT ON DATABASE %I TO ddl_user', 'Gym');
        EXECUTE format('GRANT CONNECT ON DATABASE %I TO app_user', 'Gym');
        EXECUTE format('GRANT CONNECT ON DATABASE %I TO exporter', 'Gym');
    ELSE
        RAISE NOTICE 'BD "Gym" no encontrada; crea la BD primero o ajusta el script.';
    END IF;
END $$;

-- Hacer a ddl_user OWNER del schema public y dar permisos mínimos
ALTER SCHEMA public OWNER TO ddl_user;
GRANT USAGE ON SCHEMA public TO app_user, exporter;
GRANT CREATE ON SCHEMA public TO ddl_user;

-- Privilegios iniciales sobre objetos existentes (si hay)
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO exporter;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user, exporter;

-- Para que app_user pueda hacer CRUD en tablas nuevas
ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO app_user;

-- DEFAULT PRIVILEGES: cuando ddl_user cree tablas nuevas, dar SELECT a exporter
ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
    GRANT SELECT ON TABLES TO exporter;

ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO app_user, exporter;

-- Dar rol de monitor al exporter solo si no es miembro de pg_monitor para hacerlo idempotente.
DO $$
BEGIN
    IF NOT EXISTS (
    SELECT 1
    FROM pg_auth_members m
        JOIN pg_roles r_parent ON r_parent.oid = m.roleid
        JOIN pg_roles r_child  ON r_child.oid  = m.member
    WHERE r_parent.rolname = 'pg_monitor'
        AND r_child.rolname  = 'exporter'
    ) THEN
        GRANT pg_monitor TO exporter;
    END IF;
END $$;

-- Hardening: evitar que exporter use el search_path de public por defecto
ALTER ROLE exporter SET search_path = pg_catalog;
