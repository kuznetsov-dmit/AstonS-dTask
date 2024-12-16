#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    \c library;
    \i /docker-entrypoint-initdb.d/schema.sql
EOSQL