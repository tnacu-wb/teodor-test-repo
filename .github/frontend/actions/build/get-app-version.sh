#!/usr/bin/env bash
set -euo pipefail

app_name=${1:?app name is required}
case "$app_name" in
  business-booker|ccui|premier-inn) ;;
  *)
    printf 'Unsupported frontend app: %s\n' "$app_name" >&2
    exit 1
    ;;
esac

version=$(cd "apps/next-apps/$app_name" && npm pkg get version --workspaces=false | tr -d '"')
printf '%s\n' "$version"
printf '%s-version=%s\n' "$app_name" "$version" >> "${GITHUB_OUTPUT:?GITHUB_OUTPUT is required}"
