-- Provisions a dedicated database and schema for rules-manager-entity-service on the
-- shared Postgres instance, keeping it isolated from the 'unleash' database.
--
-- Scripts in /docker-entrypoint-initdb.d run only when the Postgres data directory is
-- empty, i.e. on a fresh container. start-compose.sh force-recreates unleash-postgres
-- (no persistent volume), so this runs on every local startup.
CREATE DATABASE rulesmanager;
\connect rulesmanager
CREATE SCHEMA IF NOT EXISTS rules_engine;
