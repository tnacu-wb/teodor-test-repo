#!/usr/bin/env bash
set -euo pipefail

# Determine the monorepo root relative to this script's location.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

cd "$REPO_ROOT"

echo "==> Preparing local Unleash import from DIT..."
if ! backend/integration-env/scripts/seed-unleash.sh; then
  echo "ERROR: Failed to prepare local Unleash import." >&2
  exit 1
fi

"backend/integration-env/scripts/prepare-otel-agent.sh"

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

build_image() {
  local image_name="$1"
  local dockerfile="$2"
  local context="$3"

  echo "==> Building Docker image ${image_name}..."
  if ! "$CONTAINER_CLI" build -f "$dockerfile" -t "$image_name" "$context"; then
    echo "ERROR: Docker image build failed for ${image_name}. Check Dockerfile syntax and disk space." >&2
    exit 1
  fi
}

build_image "ohip-adapter-service:local" "backend/integration-env/dockerfiles/ohip-adapter-service.Dockerfile" "backend/discover-search/services/ohip-adapter-service/target"
build_image "content-entity-service:local" "backend/integration-env/dockerfiles/content-entity-service.Dockerfile" "backend/discover-search/services/content-entity-service/target"
build_image "hotel-entity-service:local" "backend/integration-env/dockerfiles/hotel-entity-service.Dockerfile" "backend/discover-search/services/hotel-entity-service/target"
build_image "rules-manager-entity-service:local" "backend/integration-env/dockerfiles/rules-manager-entity-service.Dockerfile" "backend/discover-search/services/rules-manager-entity-service/target"
build_image "rules-agent-entity-service:local" "backend/integration-env/dockerfiles/rules-agent-entity-service.Dockerfile" "backend/discover-search/services/rules-agent-entity-service/target"
build_image "cdh-adapter-service:local" "backend/integration-env/dockerfiles/cdh-adapter-service.Dockerfile" "backend/identity/services/cdh-adapter-service/target"
build_image "company-entity-service:local" "backend/integration-env/dockerfiles/company-entity-service.Dockerfile" "backend/identity/services/company-entity-service/target"
build_image "piba-account-service-opera:local" "backend/integration-env/dockerfiles/piba-account-service-opera.Dockerfile" "backend/identity/services/piba-account-service-opera/target"
build_image "spending-entity-service:local" "backend/integration-env/dockerfiles/spending-entity-service.Dockerfile" "backend/identity/services/spending-entity-service/target"
build_image "basket-service:local" "backend/integration-env/dockerfiles/basket-service.Dockerfile" "backend/book-pay/services/basket-service/target"
build_image "hotel-reservation-entity-service:local" "backend/integration-env/dockerfiles/hotel-reservation-entity-service.Dockerfile" "backend/manage-modify/services/hotel-reservation-entity-service/target"
build_image "hotel-countries-service-opera:local" "backend/integration-env/dockerfiles/hotel-countries-service-opera.Dockerfile" "backend/identity/services/hotel-countries-service-opera/target"
build_image "threec-payment-service-opera:local" "backend/integration-env/dockerfiles/threec-payment-service-opera.Dockerfile" "backend/book-pay/services/threec-payment-service-opera/target"
build_image "hotel-account-service-opera:local" "backend/integration-env/dockerfiles/hotel-account-service-opera.Dockerfile" "backend/identity/services/hotel-account-service-opera/target"
build_image "company-service-opera:local" "backend/integration-env/dockerfiles/company-service-opera.Dockerfile" "backend/identity/services/company-service-opera/target"
build_image "payment-methods-entity-service:local" "backend/integration-env/dockerfiles/payment-methods-entity-service.Dockerfile" "backend/book-pay/services/payment-methods-entity-service/target"
build_image "payment-orchestration-service:local" "backend/integration-env/dockerfiles/payment-orchestration-service.Dockerfile" "backend/book-pay/services/payment-orchestration-service/target"

echo "==> Docker image build succeeded."
echo "    Image: ohip-adapter-service:local"
echo "    Image: content-entity-service:local"
echo "    Image: hotel-entity-service:local"
echo "    Image: rules-manager-entity-service:local"
echo "    Image: rules-agent-entity-service:local"
echo "    Image: cdh-adapter-service:local"
echo "    Image: company-entity-service:local"
echo "    Image: piba-account-service-opera:local"
echo "    Image: spending-entity-service:local"
echo "    Image: basket-service:local"
echo "    Image: hotel-reservation-entity-service:local"
echo "    Image: hotel-countries-service-opera:local"
echo "    Image: threec-payment-service-opera:local"
echo "    Image: hotel-account-service-opera:local"
echo "    Image: company-service-opera:local"
echo "    Image: payment-methods-entity-service:local"
echo "    Image: payment-orchestration-service:local"
