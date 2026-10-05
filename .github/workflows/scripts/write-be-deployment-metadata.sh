#!/usr/bin/env bash
set -euo pipefail

: "${SERVICE_NAME:?SERVICE_NAME must be set}"
: "${IMAGE_TAG:?IMAGE_TAG must be set}"
: "${SOURCE_SHA:?SOURCE_SHA must be set}"
: "${OUTPUT_DIR:?OUTPUT_DIR must be set}"

mkdir -p "$OUTPUT_DIR"
printf '{"appName":"%s","imageTag":"%s","sourceSha":"%s"}\n' \
  "$SERVICE_NAME" "$IMAGE_TAG" "$SOURCE_SHA" \
  > "$OUTPUT_DIR/${SERVICE_NAME}.json"
