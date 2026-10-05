#!/usr/bin/env bash

set -euo pipefail

# True when <service> has an ephemeral-values.yaml or values.yaml under any squad.
is_deployable() {
  find backend -type f \
    \( -path "*/services/$1/infrastructure/helm/ephemeral-values.yaml" \
    -o -path "*/services/$1/infrastructure/helm/values.yaml" \) \
    | grep -q .
}

# Fail closed on anything that is not a strict service slug.
validate() {
  if [[ ! "$1" =~ ^[a-z0-9][a-z0-9-]*$ ]]; then
    echo "::error::Rejected service name '$1' (allowed: lowercase letters, digits, hyphens)." >&2
    exit 1
  fi
}

result='[]'

if [[ -n "${SERVICES:-}" ]]; then
  # Filter a provided set (e.g. the PR's changed services) down to the deployable ones.
  while IFS= read -r svc; do
    [[ -z "$svc" ]] && continue
    validate "$svc"
    if is_deployable "$svc"; then
      result=$(jq -c --arg s "$svc" '. + [$s]' <<<"$result")
    else
      echo "::notice::'$svc' will build but not deploy — no ephemeral-values.yaml/values.yaml (not ephemerally deployable yet)." >&2
    fi
  done < <(jq -r '.[]' <<<"$SERVICES")
else
  # Enumerate every deployable service in the tree.
  while IFS= read -r svc; do
    [[ -z "$svc" ]] && continue
    validate "$svc"
    result=$(jq -c --arg s "$svc" '. + [$s]' <<<"$result")
  done < <(
    find backend -type f \
      \( -path '*/services/*/infrastructure/helm/ephemeral-values.yaml' \
      -o -path '*/services/*/infrastructure/helm/values.yaml' \) \
      | sed -E 's#^.*/services/([^/]+)/infrastructure/helm/(ephemeral-values|values)\.yaml$#\1#' \
      | sort -u
  )
fi

printf '%s\n' "$result"
