#!/usr/bin/env bash
# detect-changed-modules.sh
#
# Classify files changed between $BASE_SHA and $HEAD_SHA into matrix
# outputs that drive the downstream build jobs in ci.yaml.
#
# Inputs (env):
#   BASE_SHA       Base commit SHA of the diff range
#   HEAD_SHA       Head commit SHA of the diff range
#   GITHUB_OUTPUT  Path of the GitHub Actions step-output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   services=<JSON array of service directory names>
#   libraries=<JSON array of library directory names>
#   graphql=<"true"|"false">  (plain string, not JSON)
#   helm=<JSON array of helm chart directory names>
#   tools=<JSON array of tool directory names>
# services and libraries are single-line, deduplicated JSON arrays (either may be `[]`).
#
# Classification rules:
#   - `backend/pom.xml` changed → services = all services, libraries = all libraries
#   - `backend/<squad>/services/<svc>/**` → <svc> added to services
#   - `backend/<squad>/libs/<lib>/**`     → <lib> added to libraries; for each, run
#                                           reverse-deps.sh under a 60s timeout and union
#                                           its result into services
#   - `graphql/**` changed → graphql = true
#
# Exit codes (propagated to fail the change-detection job):
#   0    success
#   1    parse error (reverse-deps.sh or POM parsing)
#   124  reverse-deps.sh timed out (>60s)
#
# Stdout is reserved for `::error::` / `::warning::` GitHub annotations.

set -euo pipefail

: "${BASE_SHA:?BASE_SHA must be set}"
: "${HEAD_SHA:?HEAD_SHA must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REVERSE_DEPS="$SCRIPT_DIR/reverse-deps.sh"

# Discover all service directory names from backend/*/services/<svc>/pom.xml.
ALL_SERVICES=$(find backend/*/services -maxdepth 2 -name pom.xml -exec dirname {} \; \
  | xargs -I{} basename {} \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# Discover all library directory names from backend/*/libs/<lib>/pom.xml.
ALL_LIBRARIES=$(find backend/*/libs -maxdepth 2 -name pom.xml -exec dirname {} \; \
  | xargs -I{} basename {} \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

BASE_SHA=$(git merge-base "$BASE_SHA" "$HEAD_SHA")
CHANGED_FILES=$(git diff --name-only "$BASE_SHA" "$HEAD_SHA")

# --- GraphQL stack detection ---
GRAPHQL_CHANGED="false"
if grep -q '^graphql/' <<<"$CHANGED_FILES"; then
  GRAPHQL_CHANGED="true"
fi

# --- Tools detection ---
TOOLS=$({ grep -oE '^tools/[^/]+/' <<<"$CHANGED_FILES" || true; } \
  | sed 's:^tools/::; s:/$::' | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# --- Helm/Infrastructure detection ---
HELM_CHARTS=$({ grep -oE '^infra/[^/]+/' <<<"$CHANGED_FILES" || true; } \
  | sed 's:^infra/::; s:/$::' | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# --- Parent POM infrastructure detection ---
# Service parent change → rebuild all services
SVC_PARENT_CHANGED="false"
if grep -qx 'backend/parents/service-parent/pom.xml' <<<"$CHANGED_FILES"; then
  SVC_PARENT_CHANGED="true"
fi

# Library parent change → rebuild all libraries
LIB_PARENT_CHANGED="false"
if grep -qx 'backend/parents/library-parent/pom.xml' <<<"$CHANGED_FILES"; then
  LIB_PARENT_CHANGED="true"
fi

# Any other parent under backend/parents/ changed → find services that use it
OTHER_PARENT_SERVICES='[]'
CHANGED_PARENTS=$({ grep -oE '^backend/parents/[^/]+/pom\.xml$' <<<"$CHANGED_FILES" || true; } \
  | grep -v 'service-parent\|library-parent' || true)
for PARENT_POM in $CHANGED_PARENTS; do
  # Extract the artifactId from the changed parent POM
  PARENT_ARTIFACT=$(sed -n '/<parent>/,/<\/parent>/d; s/.*<artifactId>\(.*\)<\/artifactId>.*/\1/p' "$PARENT_POM" | head -1 | tr -d '[:space:]' || true)
  if [[ -n "$PARENT_ARTIFACT" ]]; then
    # Find services whose POM references this parent artifactId
    MATCHED=$(grep -rl "<artifactId>$PARENT_ARTIFACT</artifactId>" backend/*/services/*/pom.xml 2>/dev/null \
      | while read -r f; do basename "$(dirname "$f")"; done \
      | jq -R -s -c 'split("\n") | map(select(length > 0))' || echo '[]')
    OTHER_PARENT_SERVICES=$(jq -c -s 'add | unique' <<<"$OTHER_PARENT_SERVICES $MATCHED")
  fi
done

# backend/pom.xml change ⇒ rebuild every service AND every library.
if grep -qx 'backend/pom.xml' <<<"$CHANGED_FILES"; then
  {
    echo "services=$ALL_SERVICES"
    echo "libraries=$ALL_LIBRARIES"
    echo "graphql=$GRAPHQL_CHANGED"
    echo "helm=$HELM_CHARTS"
    echo "tools=$TOOLS"
  } >> "$GITHUB_OUTPUT"
  exit 0
fi

# Direct service-path changes.
DIRECT_SERVICES=$({ grep -oE '^backend/[^/]+/services/[^/]+/' <<<"$CHANGED_FILES" || true; } \
  | sed 's:^backend/[^/]*/services/::; s:/$::' | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# Library-path changes.
CHANGED_LIBS=$({ grep -oE '^backend/[^/]+/libs/[^/]+/' <<<"$CHANGED_FILES" || true; } \
  | sed 's:^backend/[^/]*/libs/::; s:/$::' | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# Reverse-dependency lookup per changed library, with a 60s per-call budget.
REVERSE_DEPS_JSON='[]'
BUILDABLE_LIBS='[]'
for LIB in $(jq -r '.[]' <<<"$CHANGED_LIBS"); do
  # Resolve POM path dynamically — library may live under any squad directory.
  POM=$(find backend/*/libs/"$LIB"/pom.xml 2>/dev/null | head -1)

  if [[ -n "$POM" && -f "$POM" ]]; then
    # Library exists on HEAD — read coordinates from the current file.
    # Use sed to extract the project-level groupId/artifactId (skip <parent> block).
    GROUP=$(sed -n '/<parent>/,/<\/parent>/d; s/.*<groupId>\(.*\)<\/groupId>.*/\1/p' "$POM" | head -1 | tr -d '[:space:]' || true)
    ARTIFACT=$(sed -n '/<parent>/,/<\/parent>/d; s/.*<artifactId>\(.*\)<\/artifactId>.*/\1/p' "$POM" | head -1 | tr -d '[:space:]' || true)
    if [[ -z "$GROUP" || -z "$ARTIFACT" ]]; then
      echo "::error::missing groupId/artifactId in $POM" >&2
      exit 1
    fi
    # Library is buildable — include it in the libraries matrix.
    BUILDABLE_LIBS=$(jq -c ". + [\"$LIB\"]" <<<"$BUILDABLE_LIBS")
  else
    # Library was deleted in this PR — read coordinates from the base commit
    # so we can still find consumer services that need to be rebuilt (they'll
    # fail with a missing-dependency error, which is the correct signal).
    BASE_POM_PATH=$(git ls-tree -r --name-only "$BASE_SHA" | grep "backend/.*/libs/$LIB/pom.xml" | head -1)
    if [[ -z "$BASE_POM_PATH" ]]; then
      # Can't resolve POM from base either — skip entirely.
      continue
    fi
    POM_CONTENT=$(git show "$BASE_SHA:$BASE_POM_PATH" 2>/dev/null || true)
    if [[ -z "$POM_CONTENT" ]]; then
      # Can't resolve POM from base either — skip entirely.
      continue
    fi
    GROUP=$(echo "$POM_CONTENT" | sed -n '/<parent>/,/<\/parent>/d; s/.*<groupId>\(.*\)<\/groupId>.*/\1/p' | head -1 | tr -d '[:space:]' || true)
    ARTIFACT=$(echo "$POM_CONTENT" | sed -n '/<parent>/,/<\/parent>/d; s/.*<artifactId>\(.*\)<\/artifactId>.*/\1/p' | head -1 | tr -d '[:space:]' || true)
    if [[ -z "$GROUP" || -z "$ARTIFACT" ]]; then
      # Can't resolve coordinates from base either — skip entirely.
      continue
    fi
    # Don't add to BUILDABLE_LIBS — there's nothing to build for a deleted lib.
  fi

  rc=0
  if command -v timeout &>/dev/null; then
    REV=$(timeout 60 "$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
  elif command -v gtimeout &>/dev/null; then
    REV=$(gtimeout 60 "$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
  else
    # No timeout command available (e.g., macOS without coreutils).
    # Run without timeout — CI runners have GNU timeout preinstalled.
    REV=$("$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
  fi
  if (( rc != 0 )); then
    if (( rc == 124 )); then
      echo "::error::reverse-deps.sh timed out (>60s) for $GROUP:$ARTIFACT" >&2
    else
      echo "::error::reverse-deps.sh failed (exit $rc) for $GROUP:$ARTIFACT" >&2
    fi
    exit "$rc"
  fi

  REVERSE_DEPS_JSON=$(jq -c -s 'add | unique' <<<"$REVERSE_DEPS_JSON $REV")
done

SERVICES=$(jq -c -s 'add | unique' <<<"$DIRECT_SERVICES $REVERSE_DEPS_JSON")

# Apply parent POM flags to final outputs.
# Service parent changed → all services
if [[ "$SVC_PARENT_CHANGED" == "true" ]]; then
  SERVICES="$ALL_SERVICES"
fi

# Other parents changed → add their consumer services
if [[ "$OTHER_PARENT_SERVICES" != "[]" ]]; then
  SERVICES=$(jq -c -s 'add | unique' <<<"$SERVICES $OTHER_PARENT_SERVICES")
fi

# Library parent changed → all libraries (services that consume them are found via reverse-deps)
if [[ "$LIB_PARENT_CHANGED" == "true" ]]; then
  BUILDABLE_LIBS="$ALL_LIBRARIES"
fi

{
  echo "services=$SERVICES"
  echo "libraries=$(jq -c 'unique' <<<"$BUILDABLE_LIBS")"
  echo "graphql=$GRAPHQL_CHANGED"
  echo "helm=$HELM_CHARTS"
  echo "tools=$TOOLS"
} >> "$GITHUB_OUTPUT"
