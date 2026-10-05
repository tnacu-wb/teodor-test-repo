#!/usr/bin/env bash
set -euo pipefail

: "${METADATA_FILE:?METADATA_FILE must be set}"
: "${EXPECTED_SERVICE:?EXPECTED_SERVICE must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

app_name=$(jq -er '.appName' "$METADATA_FILE")
image_tag=$(jq -er '.imageTag' "$METADATA_FILE")

if [[ "$app_name" != "$EXPECTED_SERVICE" ]]; then
  echo "::error::Deployment metadata belongs to '$app_name', expected '$EXPECTED_SERVICE'."
  exit 1
fi

echo "image-tag=$image_tag" >> "$GITHUB_OUTPUT"
