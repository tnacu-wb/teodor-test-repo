#!/usr/bin/env bash
set -euo pipefail

# Determine the monorepo root relative to this script's location.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"
COMPOSE_FILE="backend/integration-env/docker-compose.yml"
UNLEASH_IMPORT_FILE="backend/integration-env/unleash/import/client-features.import.json"

cd "$REPO_ROOT"

"backend/integration-env/scripts/prepare-otel-agent.sh"

if [[ ! -s "$UNLEASH_IMPORT_FILE" ]]; then
  echo "ERROR: Missing local Unleash import file: $UNLEASH_IMPORT_FILE" >&2
  echo "       Generate it with REAL_UNLEASH_TOKEN='<token>' backend/integration-env/scripts/seed-unleash.sh" >&2
  exit 1
fi

CONTAINER_CLI="${CONTAINER_CLI:-}"
if [[ -z "$CONTAINER_CLI" ]]; then
  if command -v docker >/dev/null 2>&1; then
    CONTAINER_CLI="docker"
  elif command -v podman >/dev/null 2>&1; then
    CONTAINER_CLI="podman"
  else
    echo "ERROR: Neither docker nor podman was found on PATH." >&2
    exit 1
  fi
fi

compose_cmd=()
if [[ -n "${COMPOSE_CMD:-}" ]]; then
  # shellcheck disable=SC2206
  compose_cmd=($COMPOSE_CMD)
elif "$CONTAINER_CLI" compose version >/dev/null 2>&1; then
  compose_cmd=("$CONTAINER_CLI" "compose")
elif command -v docker-compose >/dev/null 2>&1; then
  compose_cmd=("docker-compose")
elif command -v podman-compose >/dev/null 2>&1; then
  compose_cmd=("podman-compose")
else
  echo "ERROR: No Docker Compose-compatible command was found." >&2
  echo "       Install docker compose, docker-compose, podman compose, or podman-compose." >&2
  exit 1
fi

echo "==> Validating Docker Compose configuration..."
if ! "${compose_cmd[@]}" -f "$COMPOSE_FILE" config >/dev/null; then
  echo "ERROR: Docker Compose configuration validation failed." >&2
  exit 1
fi

echo "==> Stopping services connected to Unleash/Postgres before recreating them..."
if ! "${compose_cmd[@]}" -f "$COMPOSE_FILE" stop \
  basket-service \
  hotel-reservation-entity-service \
  hotel-entity-service \
  ohip-adapter-service \
  content-entity-service \
  cdh-adapter-service \
  rules-agent-entity-service \
  rules-manager-entity-service \
  hotel-countries-service-opera \
  threec-payment-service-opera \
  hotel-account-service-opera \
  company-service-opera \
  payment-methods-entity-service \
  payment-orchestration-service \
  temporal; then
  echo "ERROR: Failed to stop Unleash/Postgres client services before recreating infrastructure." >&2
  exit 1
fi

echo "==> Starting Unleash with the generated feature-flag snapshot..."
if ! "${compose_cmd[@]}" -f "$COMPOSE_FILE" up -d --no-build --force-recreate --remove-orphans unleash-postgres unleash; then
  echo "ERROR: Local Unleash failed to start." >&2
  exit 1
fi

echo "==> Starting Docker Compose stack..."
if ! "${compose_cmd[@]}" -f "$COMPOSE_FILE" up -d --no-build --remove-orphans; then
  echo "ERROR: Docker Compose stack failed to start." >&2
  echo "       If local service images are missing, build them first with backend/integration-env/scripts/build-images.sh." >&2
  exit 1
fi

wait_for_healthy() {
  local service_name="$1"
  local app_container
  local app_health

  echo "==> Waiting for ${service_name} to become healthy..."
  app_container="$("${compose_cmd[@]}" -f "$COMPOSE_FILE" ps -q "$service_name")"
  if [[ -z "$app_container" ]]; then
    echo "ERROR: Could not find ${service_name} container after compose up." >&2
    exit 1
  fi

  for _ in {1..24}; do
    app_health="$("$CONTAINER_CLI" inspect "$app_container" --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}')"
    if [[ "$app_health" == "healthy" ]]; then
      break
    fi
    if [[ "$app_health" == "unhealthy" ]]; then
      echo "ERROR: ${service_name} became unhealthy." >&2
      "${compose_cmd[@]}" -f "$COMPOSE_FILE" logs --tail=120 "$service_name" >&2
      exit 1
    fi
    sleep 5
  done

  app_health="$("$CONTAINER_CLI" inspect "$app_container" --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}')"
  if [[ "$app_health" != "healthy" ]]; then
    echo "ERROR: ${service_name} did not become healthy within 120 seconds. Current status: $app_health" >&2
    "${compose_cmd[@]}" -f "$COMPOSE_FILE" logs --tail=120 "$service_name" >&2
    exit 1
  fi
}

wait_for_healthy "unleash"
wait_for_healthy "rules-manager-entity-service"
wait_for_healthy "rules-agent-entity-service"
wait_for_healthy "ohip-adapter-service"
wait_for_healthy "content-entity-service"
wait_for_healthy "cdh-adapter-service"
wait_for_healthy "company-entity-service"
wait_for_healthy "hotel-entity-service"
wait_for_healthy "piba-account-service-opera"
wait_for_healthy "spending-entity-service"
wait_for_healthy "basket-service"
wait_for_healthy "hotel-reservation-entity-service"
wait_for_healthy "hotel-countries-service-opera"
wait_for_healthy "threec-payment-service-opera"
wait_for_healthy "hotel-account-service-opera"
wait_for_healthy "company-service-opera"
wait_for_healthy "payment-methods-entity-service"
wait_for_healthy "temporal"
wait_for_healthy "payment-orchestration-service"

echo "==> Docker Compose startup succeeded."
echo "    Jaeger UI: http://localhost:16686"
echo "    Unleash UI: http://localhost:4242"
echo "    App health: http://localhost:9100/ohip/actuator/health"
echo "    Content health: http://localhost:9106/v1/content/actuator/health"
echo "    Rules Manager health: http://localhost:9105/rmg/actuator/health"
echo "    Rules Agent health: http://localhost:9108/v1/rules/actuator/health"
echo "    CDH health: http://localhost:9119/v1/cdh/actuator/health"
echo "    Company Entity health: http://localhost:9118/v1/companies/actuator/health"
echo "    Hotel Entity health: http://localhost:9102/v1/hotels/actuator/health"
echo "    PIBA health: http://localhost:9064/piba-account-service/actuator/health"
echo "    Spending health: http://localhost:9132/v1/spending/actuator/health"
echo "    Basket health: http://localhost:9104/v1/baskets/actuator/health"
echo "    Hotel Reservation health: http://localhost:9103/v1/reservations/actuator/health"
echo "    Hotel Countries health: http://localhost:9031/hotel-countries-service/actuator/health"
echo "    ThreeC Payment health: http://localhost:9001/threec-payment-service/actuator/health"
echo "    Hotel Account health: http://localhost:9020/hotel-account-service/actuator/health"
echo "    Company Service health: http://localhost:9022/company-service/actuator/health"
echo "    Payment Methods health: http://localhost:9107/v1/payment-methods/actuator/health"
echo "    Payment Orchestration health: http://localhost:9200/payment-orchestrator/actuator/health"
echo "    WireMock Opera: http://localhost:8443/__admin/mappings"
echo "    WireMock CDH/Auth0: http://localhost:8445/__admin/mappings"
echo "    WireMock Worldline/Datatrans: http://localhost:8446/__admin/mappings"
