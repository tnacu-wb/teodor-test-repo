#!/usr/bin/env bash
set -euo pipefail

for required_name in APP_NAME APP_VERSION REF_NAME COMMIT_SHA GITHUB_OUTPUT; do
  if [[ -z "${!required_name:-}" ]]; then
    printf '%s is required\n' "$required_name" >&2
    exit 1
  fi
done

case "$APP_NAME" in
  business-booker | ccui | premier-inn)
    ;;
  *)
    printf 'Unsupported frontend application: %s\n' "$APP_NAME" >&2
    exit 1
    ;;
esac

workspace_root=${GITHUB_WORKSPACE:-$(pwd -P)}
script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source-path=SCRIPTDIR
# shellcheck source=image-tag.sh
source "$script_dir/image-tag.sh"
docker_file="$workspace_root/frontend/pi-front-end-applications/Dockerfile"
app_root="$workspace_root/apps/next-apps/$APP_NAME"

required_paths=(
  "$docker_file"
  "$app_root/public"
  "$app_root/.next/standalone"
  "$app_root/.next/static"
  "$app_root/.env.production"
  "$workspace_root/scripts/runtimeEnvVars"
)

for required_path in "${required_paths[@]}"; do
  if [[ ! -e "$required_path" ]]; then
    printf 'Required frontend image path does not exist: %s\n' "$required_path" >&2
    exit 1
  fi
done

# Inputs are supplied by the composite action and validated by the required_name loop above.
# shellcheck disable=SC2153
image_tag=$(generate_frontend_image_tag \
  "$APP_VERSION" \
  "$REF_NAME" \
  "$COMMIT_SHA" \
  "" \
  "${SEMANTIC_VERSION:-}")
dockerhub_image="whitbreaddigital/${APP_NAME}:${image_tag}"
printf 'image-tag=%s\n' "$image_tag" >> "$GITHUB_OUTPUT"

docker build \
  --build-arg "APP_NAME=$APP_NAME" \
  --rm=true \
  -t "$dockerhub_image" \
  -f "$docker_file" \
  "$workspace_root"

if [[ "${PUSH_DOCKERHUB:-false}" == "true" ]]; then
  docker push "$dockerhub_image"
fi

if [[ "${PUSH_ECR:-false}" == "true" ]]; then
  : "${ECR_REGISTRY:?ECR_REGISTRY is required when PUSH_ECR is true}"
  ecr_image="${ECR_REGISTRY}/whitbreaddigital/${APP_NAME}:${image_tag}"
  docker tag "$dockerhub_image" "$ecr_image"
  docker push "$ecr_image"
fi
