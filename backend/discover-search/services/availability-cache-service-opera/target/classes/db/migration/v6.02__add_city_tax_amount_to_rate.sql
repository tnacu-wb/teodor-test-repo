--liquibase formatted sql

--changeset Santhosh_John:01092025-v6.02__add_city_tax_amount_to_rate-1 comment:Adds a new column to the rate table to store the amount including city tax.
ALTER TABLE avail_cache.rate
    ADD COLUMN amount_with_city_tax numeric(19, 2);

--rollback ALTER TABLE avail_cache.rate DROP COLUMN amount_with_city_tax;