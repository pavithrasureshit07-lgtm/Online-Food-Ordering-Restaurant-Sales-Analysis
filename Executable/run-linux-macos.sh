#!/usr/bin/env bash
set -euo pipefail
: "${DB_URL:=jdbc:mysql://localhost:3306/restaurant_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC}"
: "${DB_USER:=root}"
export DB_URL DB_USER
if [[ -z "${DB_PASSWORD:-}" ]]; then echo "Set DB_PASSWORD before starting." >&2; exit 1; fi
java -jar "$(dirname "$0")/restaurant-management.jar"
