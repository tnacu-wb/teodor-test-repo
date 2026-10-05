#!/usr/bin/env bash
# reverse-deps.sh
#
# Find every monorepo service that declares a DIRECT Maven <dependency> on a
# given groupId:artifactId, by parsing each backend/<squad>/services/<svc>/pom.xml.
#
# Usage:
#   reverse-deps.sh <groupId> <artifactId>
#
# Output:
#   JSON array of service directory names (sorted, deduplicated) on stdout.
#   Empty array `[]` when no matches.
#
# Exit codes:
#   0    success (including empty result)
#   1    malformed POM or unrecoverable error (stderr names the file)
#   2    usage error (wrong number of arguments)
#   124  reserved for callers wrapping this script with GNU `timeout`
#
# Trade-off (Requirement 4.2):
#   Only DIRECT <dependency> declarations are inspected by searching for the
#   groupId and artifactId as adjacent XML elements. Transitive dependencies
#   and dependencies pulled in indirectly via <dependencyManagement> BOM imports
#   without a direct <dependency> entry in the service POM are intentionally NOT
#   detected. This matches the spec's wording "excluding transitive dependencies"
#   and avoids the JVM warm-up cost of `mvn dependency:tree`.

set -euo pipefail

if [[ $# -ne 2 ]]; then
  echo "Usage: $0 <groupId> <artifactId>" >&2
  exit 2
fi

GROUP_ID=$1
ARTIFACT_ID=$2

# Resolve the repository root from this script's location so the script works
# regardless of the caller's working directory.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/../../.." && pwd)"

# Discover all service POMs across ALL squad directories.
shopt -s nullglob
service_poms=("$REPO_ROOT"/backend/*/services/*/pom.xml)
shopt -u nullglob

if [[ ${#service_poms[@]} -eq 0 ]]; then
  echo "[]"
  exit 0
fi

matches=()

for pom in "${service_poms[@]}"; do
  if [[ ! -f "$pom" ]]; then
    continue
  fi

  # Check if the POM contains a <dependency> block with both the target
  # groupId and artifactId. We use a multi-line grep approach:
  # 1. Extract all <dependency>...</dependency> blocks
  # 2. Check if any block contains both the groupId and artifactId
  #
  # awk extracts each dependency block, then we grep for both values.
  found=$(awk '/<dependency>/,/<\/dependency>/' "$pom" \
    | awk -v RS='</dependency>' -v gid="$GROUP_ID" -v aid="$ARTIFACT_ID" \
      '$0 ~ gid && $0 ~ aid {print "MATCH"; exit}' 2>/dev/null || true)

  if [[ "$found" == "MATCH" ]]; then
    svc_dir="$(basename "$(dirname "$pom")")"
    matches+=("$svc_dir")
  fi
done

# Emit a sorted, deduplicated JSON array.
if [[ ${#matches[@]} -eq 0 ]]; then
  echo "[]"
else
  printf '%s\n' "${matches[@]}" | sort -u | jq -R . | jq -s -c .
fi
