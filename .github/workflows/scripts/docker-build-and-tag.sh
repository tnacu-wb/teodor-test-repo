#!/usr/bin/env bash
# docker-build-and-tag.sh
#
# Builds a Docker image for a backend service using the compiled JAR artifact.
# Tags the image with a temporary "prototype" tag for subsequent retagging.
#
# Inputs (env):
#   SERVICE_NAME    Name of the service (e.g., basket-async-order-processor)
#   APP_VERSION     Application version (default: 1.0.0)
#
# Outputs:
#   Builds and tags Docker image: whitbreaddigital/<service>:prototype
#
# Exit codes:
#   0  Success
#   1  JAR file not found or Docker build failed

set -euo pipefail

: "${SERVICE_NAME:?SERVICE_NAME must be set}"

APP_VERSION="${APP_VERSION:-1.0.0}"
JAR_FILE="${SERVICE_NAME}-${APP_VERSION}.jar"
IMAGES_DIR="backend/infrastructure/images"

if [[ ! -f "$JAR_FILE" ]]; then
  echo "::error::JAR file not found: $JAR_FILE"
  exit 1
fi

echo "Moving $JAR_FILE to $IMAGES_DIR/"
mv "$JAR_FILE" "$IMAGES_DIR/$JAR_FILE"

cd "$IMAGES_DIR"

echo "Building Docker image for $SERVICE_NAME"
docker build \
  -t "whitbreaddigital/${SERVICE_NAME}:prototype" \
  -f Dockerfile.backend \
  --build-arg APP_NAME="$SERVICE_NAME" \
  --build-arg APP_VERSION="$APP_VERSION" \
  .

ls -lh
