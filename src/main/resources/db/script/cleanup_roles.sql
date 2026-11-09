-- cleanup_roles.sql
-- Ejecutar como superuser (postgres)

-- Revocar default privileges creados por ddl_user
BEGIN;
    ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public REVOKE SELECT, INSERT, UPDATE, DELETE ON TABLES FROM app_user;
    ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public REVOKE SELECT ON TABLES FROM exporter;
    ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public REVOKE USAGE, SELECT ON SEQUENCES FROM app_user, exporter;
COMMIT;

DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'ddl_user') THEN
            ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
                REVOKE SELECT, INSERT, UPDATE, DELETE ON TABLES FROM app_user;

            ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
            REVOKE SELECT ON TABLES FROM exporter;

            ALTER DEFAULT PRIVILEGES FOR ROLE ddl_user IN SCHEMA public
                REVOKE USAGE, SELECT ON SEQUENCES FROM app_user, exporter;
    END IF;
END $$;

-- Reasignar objetos en la BD actual a postgres
REASSIGN OWNED BY exporter TO postgres;
DROP OWNED BY exporter;

REASSIGN OWNED BY app_user TO postgres;
DROP OWNED BY app_user;

REASSIGN OWNED BY ddl_user TO postgres;
DROP OWNED BY ddl_user;

-- Borrar roles (global)
DROP ROLE IF EXISTS exporter;
DROP ROLE IF EXISTS app_user;
DROP ROLE IF EXISTS ddl_user;

-- SELECT * FROM pg_roles;