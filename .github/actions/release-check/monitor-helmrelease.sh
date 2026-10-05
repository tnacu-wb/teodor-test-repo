#!/usr/bin/env bash
set -euo pipefail

previous_release_status=$(kubectl get helmrelease "$REPO_NAME" -n "$NAMESPACE" -o jsonpath='{.status.conditions[?(@.type=="Ready")].status}')
echo "Previous release status: ${previous_release_status}"
if [[ "$previous_release_status" == "False" ]]; then
  sleep 30
fi

while [[ $SECONDS -le $CHECK_TIMEOUT ]]; do
  flux get helmrelease "$REPO_NAME" -n "$NAMESPACE"
  release_status=$(kubectl get helmrelease "$REPO_NAME" -n "$NAMESPACE" -o jsonpath='{@.status.conditions[?(@.type == "Ready")].status}')
  cluster_image_tag=$(kubectl get helmrelease "$REPO_NAME" -n "$NAMESPACE" -o jsonpath='{@.spec.values.image.tag}')
  echo "Release status is [$release_status]"
  echo "Image tag deployed is:       [$cluster_image_tag]"
  echo "Image tag to be deployed is: [$IMAGE_TAG]"
  if [[ "$cluster_image_tag" == "$IMAGE_TAG" && "$release_status" == "True" ]]; then
    echo "Release succeeded"
    exit 0
  fi
  if [[ "$release_status" == "False" ]]; then
    kubectl get helmrelease "$REPO_NAME" -n "$NAMESPACE" -o jsonpath='{@.status.conditions[?(@.type == "Released")].message}'
    exit 1
  fi
  sleep 10
done

echo "The requested image tag was not deployed in the configured timeframe"
exit 1
