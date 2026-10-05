--liquibase formatted sql

--changeset Santhosh_John:01092025-v1.04__refactor_user_creation_logic-1 comment:Refactors the lambda_user_creation procedure to handle user and role creation idempotently.
CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation()
AS
'DECLARE
BEGIN
    IF EXISTS (SELECT
               FROM pg_user
               WHERE usename = ''lambda_postgres'')
    THEN
        RAISE NOTICE ''SKIP ROLE AND USERNAME CREATION!'';
    ELSE
        BEGIN
            IF EXISTS (SELECT
                       FROM pg_roles
                       WHERE rolname = ''lambda_postgres'')
            THEN
                RAISE NOTICE ''SKIP ROLE CREATION!'';
            ELSE
                BEGIN
                    CREATE ROLE readwrite;
                    GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avail_cache TO readwrite;
                    CREATE USER lambda_postgres WITH LOGIN;
                    GRANT readwrite TO lambda_postgres;
                    GRANT rds_iam TO lambda_postgres;
                    GRANT CREATE ON SCHEMA avail_cache TO lambda_postgres;
                    GRANT USAGE ON SCHEMA avail_cache TO lambda_postgres;
                EXCEPTION
                    WHEN duplicate_object THEN RAISE NOTICE ''%, moving to next statement'', SQLERRM USING ERRCODE = SQLSTATE;
                END;
            END IF;
        END;
    END IF;
END;
' LANGUAGE PLPGSQL;

CALL avail_cache.lambda_user_creation();

--rollback CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation() AS 'DECLARE BEGIN IF EXISTS (SELECT FROM pg_roles WHERE rolname = ''lambda_postgres'') THEN EXECUTE "REASSIGN OWNED BY lambda_postgres TO availabilitycache"; EXECUTE "DROP OWNED BY lambda_postgres"; EXECUTE "DROP USER IF EXISTS lambda_postgres"; EXECUTE "DROP ROLE IF EXISTS readwrite"; END IF; END;' LANGUAGE PLPGSQL;
--rollback CALL avail_cache.lambda_user_creation();
--rollback CREATE ROLE readwrite;
--rollback GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avail_cache TO readwrite;
--rollback CREATE USER lambda_postgres WITH LOGIN;
--rollback GRANT readwrite TO lambda_postgres;
--rollback GRANT rds_iam TO lambda_postgres;
--rollback GRANT CREATE ON SCHEMA avail_cache TO lambda_postgres;
--rollback GRANT USAGE ON SCHEMA avail_cache TO lambda_postgres;