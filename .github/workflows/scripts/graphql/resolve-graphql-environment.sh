#!/usr/bin/env bash
set -euo pipefail

: "${REF:?REF must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

environment=DEV
case "$REF" in
  refs/heads/release/*)
    environment=UAT
    ;;
  refs/heads/hotfix/release*)
    environment=DEMO
    ;;
esac

echo "environment=$environment" >> "$GITHUB_OUTPUT"
echo "environment-lower=$(printf '%s' "$environment" | tr '[:upper:]' '[:lower:]')" >> "$GITHUB_OUTPUT"
