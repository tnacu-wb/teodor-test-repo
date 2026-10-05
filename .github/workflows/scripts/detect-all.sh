#!/usr/bin/env bash
set -euo pipefail

: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

ENABLED_SERVICES_FILE="${CD_ENABLED_SERVICES_FILE:-.github/cd-enabled-services.json}"

if [[ ! -f "$ENABLED_SERVICES_FILE" ]]; then
  echo "::error::CD-enabled services file not found: $ENABLED_SERVICES_FILE" >&2
  exit 1
fi

if ! jq -e '
  type == "array" and
  length == (unique | length) and
  all(.[]; type == "string" and test("^[a-z0-9][a-z0-9-]*$"))
' "$ENABLED_SERVICES_FILE" >/dev/null; then
  echo "::error::$ENABLED_SERVICES_FILE must be a duplicate-free JSON array of lowercase service slugs." >&2
  exit 1
fi

ALL_SERVICES=$(jq -c 'sort' "$ENABLED_SERVICES_FILE")

while IFS= read -r service; do
  [[ -z "$service" ]] && continue
  shopt -s nullglob
  matches=(backend/*/services/"$service"/pom.xml)
  shopt -u nullglob

  if [[ ${#matches[@]} -eq 0 ]]; then
    echo "::error::CD-enabled service '$service' does not exist under backend/*/services/." >&2
    exit 1
  fi
  if [[ ${#matches[@]} -gt 1 ]]; then
    echo "::error::CD-enabled service '$service' is ambiguous: ${matches[*]}" >&2
    exit 1
  fi
done < <(jq -r '.[]' "$ENABLED_SERVICES_FILE")

ALL_LIBRARIES=$(find backend/*/libs -maxdepth 2 -name pom.xml -print0 \
  | while IFS= read -r -d '' pom; do basename "$(dirname "$pom")"; done \
  | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

NEW_SERVICES='[]'
if [[ "${EVENT_NAME:-}" == "push" && -n "${BEFORE_SHA:-}" && ! "$BEFORE_SHA" =~ ^0+$ ]]; then
  NEW_SERVICES=$(git diff --no-renames --diff-filter=A --name-only "$BEFORE_SHA" HEAD \
    | { grep -E '^backend/[^/]+/services/[^/]+/pom\.xml$' || true; } \
    | sed -E 's#^backend/[^/]+/services/([^/]+)/pom\.xml$#\1#' \
    | sort -u \
    | jq -R -s -c 'split("\n") | map(select(length > 0))')
fi

{
  echo "all_services=$ALL_SERVICES"
  echo "all_libraries=$ALL_LIBRARIES"
  echo "new_services=$NEW_SERVICES"
} >> "$GITHUB_OUTPUT"
