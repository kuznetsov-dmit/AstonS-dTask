#!/bin/bash

# Создаем необходимые директории
mkdir -p docker/postgres/init
mkdir -p docker/postgres/data

# Копируем SQL скрипт
cp src/main/resources/v1_initial_schema.sql docker/postgres/init/schema.sql

# Даем права на выполнение скрипту инициализации
chmod +x docker/postgres/init/01-init.sh

# Запускаем Docker Compose
docker-compose up -d