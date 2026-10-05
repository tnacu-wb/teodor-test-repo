--liquibase formatted sql

--changeset Santhosh_John:01092025-v1.03__update_lambda_user_creation_procedure-1 comment:Updates the cleanup procedure to reassign object ownership to 'availabilitycache' instead of 'postgres'.
CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation()
AS
'DECLARE
BEGIN
    IF EXISTS (SELECT FROM pg_roles WHERE rolname = ''lambda_postgres'')
    THEN
        EXECUTE ''REASSIGN OWNED BY lambda_postgres TO availabilitycache'';
        EXECUTE ''DROP OWNED BY lambda_postgres'';
        EXECUTE ''DROP USER IF EXISTS lambda_postgres'';
        EXECUTE ''DROP ROLE IF EXISTS readwrite'';
    END IF;
END;
' LANGUAGE PLPGSQL;

--rollback CREATE OR REPLACE PROCEDURE avail_cache.lambda_user_creation() AS 'DECLARE BEGIN IF EXISTS (SELECT FROM pg_roles WHERE rolname = ''lambda_postgres'') THEN EXECUTE "REASSIGN OWNED BY lambda_postgres TO postgres"; EXECUTE "DROP OWNED BY lambda_postgres"; EXECUTE "DROP USER IF EXISTS lambda_postgres"; EXECUTE "DROP ROLE IF EXISTS readwrite"; END IF; END;' LANGUAGE PLPGSQL;