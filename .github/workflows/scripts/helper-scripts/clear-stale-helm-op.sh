#!/usr/bin/env bash

set -euo pipefail

: "${RELEASE_NAME:?RELEASE_NAME is required}"
namespace=${NAMESPACE:-opera-be}

status=$(helm status "$RELEASE_NAME" \
  --namespace "$namespace" \
  --output json 2>/dev/null | jq -r '.info.status // empty' 2>/dev/null || true)
status=${status:-not-found}

echo "Current Helm release status: $status"
case "$status" in
  pending-install|pending-upgrade|pending-rollback)
    echo "::warning::Removing stale Helm revision in '$status' state"
    helm history "$RELEASE_NAME" --namespace "$namespace" || true
    kubectl delete configmap \
      --namespace "$namespace" \
      --selector "owner=helm,name=$RELEASE_NAME,status=$status" \
      --ignore-not-found=true
    ;;
  failed)
    echo "::warning::Uninstalling release in 'failed' state so the next deploy starts clean"
    helm history "$RELEASE_NAME" --namespace "$namespace" || true
    helm uninstall "$RELEASE_NAME" \
      --namespace "$namespace" \
      --ignore-not-found \
      --wait \
      --timeout 5m
    ;;
  *)
    echo "No stale Helm operation found"
    ;;
esac
