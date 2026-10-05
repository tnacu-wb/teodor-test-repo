#!/usr/bin/env bash
set -euo pipefail

: "${RELEASE_NAME:?RELEASE_NAME is required}"
: "${HOST_NAME:?HOST_NAME is required}"
: "${CHART_PATH:?CHART_PATH is required}"
: "${VALUES_PATH:?VALUES_PATH is required}"
: "${IMAGE_REPO:?IMAGE_REPO is required}"
: "${IMAGE_TAG:?IMAGE_TAG is required}"


helm upgrade --install "$RELEASE_NAME" "$CHART_PATH" \
  --debug \
  --namespace opera-fe \
  --create-namespace \
  --values "$VALUES_PATH" \
  --set "applicationName=$RELEASE_NAME" \
  --set "service.name=$RELEASE_NAME" \
  --set "istio.host=$HOST_NAME" \
  --set "image.repository=$IMAGE_REPO" \
  --set "image.tag=$IMAGE_TAG" \
  --set image.pullPolicy=Always \
  --wait \
  --timeout 5m
