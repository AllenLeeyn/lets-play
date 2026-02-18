#!/usr/bin/env bash
# Start local dev: Docker Compose (MongoDB), keystore if missing, then Spring Boot.
# Run from project root: ./scripts/run-dev.sh

set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
KEYSTORE="$ROOT_DIR/src/main/resources/keystore.p12"

cd "$ROOT_DIR"

echo "==> Starting Docker Compose (MongoDB)..."
docker compose up -d

echo "==> Waiting for MongoDB on 27017..."
for i in $(seq 1 30); do
  if nc -z localhost 27017 2>/dev/null; then echo "==> MongoDB is ready."; break; fi
  if [ "$i" -eq 30 ]; then echo "MongoDB did not become ready in time."; exit 1; fi
  sleep 1
done

if [ ! -f "$KEYSTORE" ]; then
  echo "==> Keystore not found; generating..."
  "$SCRIPT_DIR/gen-keystore.sh"
else
  echo "==> Keystore present, skipping gen-keystore."
fi

echo "==> Starting Spring Boot..."
exec ./mvnw spring-boot:run
