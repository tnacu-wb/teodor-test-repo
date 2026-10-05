#!/usr/bin/env bash
# detect-java-version.sh
#
# Detects the Java version for a service by checking for a .java-version file
# in the service directory. Falls back to a default version if not found.
#
# Inputs (env):
#   SERVICE_PATH    Path to the service directory
#   GITHUB_OUTPUT   Path to GitHub Actions output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   version=<java-version>
#
# Default version: 25

set -euo pipefail

: "${SERVICE_PATH:?SERVICE_PATH must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

if [[ -f "$SERVICE_PATH/.java-version" ]]; then
  version=$(cat "$SERVICE_PATH/.java-version" | tr -d '[:space:]')
  echo "Detected Java version from $SERVICE_PATH/.java-version: $version"
else
  version="25"
  echo "No .java-version file found, using default: $version"
fi

echo "version=$version" >> "$GITHUB_OUTPUT"
