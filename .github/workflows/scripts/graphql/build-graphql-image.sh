#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?APP_NAME must be set}"
: "${IMAGE_TAG:?IMAGE_TAG must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

local_image="whitbreaddigital/${APP_NAME}:${IMAGE_TAG}"

docker build --file graphql/Dockerfile --tag "$local_image" graphql

echo "image-name=$local_image" >> "$GITHUB_OUTPUT"
echo "Built local image for scanning: $local_image"
