#!/usr/bin/env bash
set -euo pipefail

: "${COMPONENT_PATH:?COMPONENT_PATH is required}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT is required}"

IFS=/ read -r -a path_parts <<< "$COMPONENT_PATH"

if [[ "$COMPONENT_PATH" == *pages* ]]; then
  component_name="pages_${path_parts[2]:-}"
elif [[ "$COMPONENT_PATH" == *next-apps* ]]; then
  component_name=${path_parts[2]:-}
elif [[ "$COMPONENT_PATH" == *pi-components-catalog* ]]; then
  component_name=${path_parts[1]:-}
else
  component_name=""
fi

if [[ -z "$component_name" ]]; then
  printf 'Cannot derive a component name from path: %s\n' "$COMPONENT_PATH" >&2
  exit 1
fi

printf 'component-name=%s\n' "$component_name" >> "$GITHUB_OUTPUT"
