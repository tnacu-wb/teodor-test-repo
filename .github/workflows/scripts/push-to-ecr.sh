#!/usr/bin/env bash
# push-to-ecr.sh
#
# Retags a Docker image and pushes it to Amazon ECR.
#
# Inputs (env):
#   SERVICE_NAME      Name of the service (e.g., basket-async-order-processor)
#   IMAGE_TAG         Tag to apply to the image (e.g., pr-42-abc1234)
#   ECR_REGISTRY      ECR registry URL (from aws-actions/amazon-ecr-login)
#   GITHUB_OUTPUT     Path to GitHub Actions output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   repository=<fully-qualified ECR repository URL>
#
# Exit codes:
#   0  Success
#   1  Docker tag or push failed

set -euo pipefail

: "${SERVICE_NAME:?SERVICE_NAME must be set}"
: "${IMAGE_TAG:?IMAGE_TAG must be set}"
: "${ECR_REGISTRY:?ECR_REGISTRY must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

ECR_REPOSITORY="${ECR_REGISTRY}/whitbreaddigital/${SERVICE_NAME}"
ECR_TAG="${ECR_REPOSITORY}:${IMAGE_TAG}"

echo "Tagging whitbreaddigital/${SERVICE_NAME}:prototype as ${ECR_TAG}"
docker tag "whitbreaddigital/${SERVICE_NAME}:prototype" "${ECR_TAG}"

echo "Pushing ${ECR_TAG} to ECR"
docker push "${ECR_TAG}"

echo "repository=${ECR_REPOSITORY}" >> "$GITHUB_OUTPUT"
echo "Successfully pushed image: ${ECR_TAG}"
