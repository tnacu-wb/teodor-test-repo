#!/usr/bin/env bash
set -euo pipefail

echo "Waiting for the Canary deployment to start"
kubectl wait --for='jsonpath={.status.phase}=Progressing' "canary/$SERVICE" -n "$NAMESPACE" --timeout 180s

while [[ $SECONDS -le 2100 ]]; do
  canary=$(kubectl get canary -n "$NAMESPACE" "$SERVICE" -o yaml)
  canary_status=$(printf '%s' "$canary" | yq '.status.phase')
  case "$canary_status" in
    Failed)
      deployment_primary=$(kubectl get deployment "$SERVICE-primary" -n "$NAMESPACE" -o yaml)
      echo "The Canary deployment has failed"
      printf 'Reason: '
      printf '%s' "$canary" | yq '.status.conditions[-1].message'
      printf 'Rolled back to image: '
      printf '%s' "$deployment_primary" | service="$SERVICE" yq '.spec.template.spec.containers[] | select(.name == env(service)).image'
      exit 1
      ;;
    Progressing)
      deployment=$(kubectl get deployment "$SERVICE" -n "$NAMESPACE" -o yaml)
      weight=$(printf '%s' "$canary" | yq '.status.canaryWeight')
      printf 'Testing image tag: '
      printf '%s' "$deployment" | service="$SERVICE" yq '.spec.template.spec.containers[] | select(.name == env(service)).image'
      echo "Deployment progressing. Traffic is routed ${weight}% to the new application"
      ;;
    Succeeded)
      deployment_primary=$(kubectl get deployment "$SERVICE-primary" -n "$NAMESPACE" -o yaml | service="$SERVICE" yq '.spec.template.spec.containers[] | select(.name == env(service)).image')
      echo "The Canary deployment has succeeded with image $deployment_primary"
      exit 0
      ;;
  esac
  sleep 30
done

echo "The Canary deployment timed out"
exit 1
