--liquibase formatted sql

--changeset Santhosh_John:01092025-v1.02__add_lambda_user_and_permissions-1 comment:Creates a utility procedure to clean up the lambda_postgres user and readwrite role if they exist.
CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation()
AS
'DECLARE
BEGIN
    IF EXISTS (SELECT
               FROM pg_roles
               WHERE rolname = ''lambda_postgres'')
    THEN
        EXECUTE ''REASSIGN OWNED BY lambda_postgres TO postgres'';
        EXECUTE ''DROP OWNED BY lambda_postgres'';
        EXECUTE ''DROP USER IF EXISTS lambda_postgres'';
        EXECUTE ''DROP ROLE IF EXISTS readwrite'';
    END IF;
END;
' LANGUAGE PLPGSQL;
--rollback DROP PROCEDURE IF EXISTS avail_cache.lambda_user_creation();

--changeset Santhosh_John:01092025-v1.02__add_lambda_user_and_permissions-2 comment:Creates the readwrite role and the lambda_postgres user for application access.
CALL avail_cache.lambda_user_creation();
CREATE ROLE readwrite;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA avail_cache TO readwrite;
CREATE USER lambda_postgres WITH LOGIN;
GRANT readwrite TO lambda_postgres;
GRANT rds_iam TO lambda_postgres;
GRANT CREATE ON SCHEMA avail_cache TO lambda_postgres;
GRANT USAGE ON SCHEMA avail_cache TO lambda_postgres;
--rollback DROP USER IF EXISTS lambda_postgres;
--rollback DROP ROLE IF EXISTS readwrite;