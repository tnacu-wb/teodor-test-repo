#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INTEGRATION_ENV_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

OTEL_JAVAAGENT_VERSION="${OTEL_JAVAAGENT_VERSION:-2.29.0}"
OTEL_JAVAAGENT_URL="${OTEL_JAVAAGENT_URL:-https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVAAGENT_VERSION}/opentelemetry-javaagent.jar}"
OTEL_DIR="$INTEGRATION_ENV_DIR/otel"
OTEL_JAVAAGENT_PATH="$OTEL_DIR/opentelemetry-javaagent.jar"

if [[ -s "$OTEL_JAVAAGENT_PATH" ]]; then
  echo "==> OpenTelemetry Java agent already exists: $OTEL_JAVAAGENT_PATH"
  exit 0
fi

if ! command -v curl >/dev/null 2>&1; then
  echo "ERROR: curl is required to download the OpenTelemetry Java agent." >&2
  exit 1
fi

mkdir -p "$OTEL_DIR"

echo "==> Downloading OpenTelemetry Java agent v${OTEL_JAVAAGENT_VERSION}..."
curl -fsSL "$OTEL_JAVAAGENT_URL" -o "$OTEL_JAVAAGENT_PATH.tmp"
mv "$OTEL_JAVAAGENT_PATH.tmp" "$OTEL_JAVAAGENT_PATH"

echo "==> OpenTelemetry Java agent ready: $OTEL_JAVAAGENT_PATH"
