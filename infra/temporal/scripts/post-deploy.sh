#!/bin/bash
# Post-deploy hook for Temporal chart
# Called by helm-deploy.yml after successful deployment
# Environment variables: NAMESPACE, CHART

set -euo pipefail

NAMESPACE="${NAMESPACE:-temporal}"
RELEASE_NAME="${CHART:-temporal}"

echo "🕐 Waiting for Temporal frontend to be ready..."
if ! kubectl wait --for=condition=ready pod \
  -n "$NAMESPACE" \
  -l app.kubernetes.io/component=frontend \
  --timeout=120s; then
  echo "⚠️  Frontend pods not ready after 120s — skipping namespace creation"
  exit 0
fi

echo "📦 Creating default Temporal namespace..."
OUTPUT=$(kubectl exec -n "$NAMESPACE" "deployment/${RELEASE_NAME}-admintools" -c admin-tools -- \
  temporal operator namespace create default --retention 72h 2>&1) || {
  if echo "$OUTPUT" | grep -qi "already exists"; then
    echo "Namespace 'default' already exists"
  else
    echo "⚠️  Failed to create namespace: $OUTPUT"
    exit 1
  fi
}

echo "✅ Temporal post-deploy complete"
