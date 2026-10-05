#!/usr/bin/env bash
set -euo pipefail

: "${APP_NAME:?APP_NAME must be set}"
: "${IMAGE_TAG:?IMAGE_TAG must be set}"
: "${ECR_REGISTRY:?ECR_REGISTRY must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

ecr_repository="${ECR_REGISTRY}/whitbreaddigital/${APP_NAME}"
ecr_image="${ecr_repository}:${IMAGE_TAG}"

docker build --file graphql/Dockerfile --tag "$ecr_image" graphql
docker push "$ecr_image"

echo "repository=$ecr_repository" >> "$GITHUB_OUTPUT"
