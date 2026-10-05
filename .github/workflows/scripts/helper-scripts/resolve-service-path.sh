#!/usr/bin/env bash
# resolve-service-path.sh
#
# Resolves the squad-based service path dynamically from the service name.
#
# Layout is <stack>/<squad>/services/<service-name>, e.g.:
#   backend/arrive-stay-leave/services/hotel-wallet-service-opera
# The squad is the segment between the stack ("backend") and "services".
#
# Inputs (env):
#   SERVICE_NAME    Name of the service (e.g., basket-async-order-processor)
#   GITHUB_OUTPUT   Path to GitHub Actions output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   service-path=<full path to service directory>
#   module-path=<path relative to backend/>
#   squad=<squad name>
#
# Exit codes:
#   0  Success
#   1  Service directory not found or ambiguous match

set -euo pipefail

: "${SERVICE_NAME:?SERVICE_NAME must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

shopt -s nullglob
matches=(backend/*/services/"$SERVICE_NAME")
shopt -u nullglob

if [[ ${#matches[@]} -eq 0 || ! -d "${matches[0]}" ]]; then
  echo "::error::Service directory not found: backend/*/services/$SERVICE_NAME"
  exit 1
fi

if [[ ${#matches[@]} -gt 1 ]]; then
  echo "::error::Ambiguous match for $SERVICE_NAME: ${matches[*]}"
  exit 1
fi

service_path="${matches[0]}"

# Strip up to and including "backend/", then take the leading segment.
# Written this way so it also holds if the repo is ever checked out
# under a prefix, e.g. digital-monorepo/backend/<squad>/services/<name>.
rel="${service_path#*backend/}"
squad="${rel%%/*}"

if [[ -z "$squad" || "$squad" == "services" ]]; then
  echo "::error::Could not determine squad from path '$service_path'"
  exit 1
fi

echo "Service path: $service_path"
echo "Squad:        $squad"

{
  echo "service-path=$service_path"
  echo "module-path=${service_path#backend/}"
  echo "squad=$squad"
} >> "$GITHUB_OUTPUT"
