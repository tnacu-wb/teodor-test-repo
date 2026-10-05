#!/usr/bin/env bash
# detect-changed-eph.sh
#
# PR-scoped change detection for ephemeral deployments.
#
# Diffs the PR head against the merge-base of the target branch and classifies
# changed paths into backend services and frontend apps. Only paths that live
# under the recognised stack roots are considered; shared infrastructure,
# workflow changes, etc. do not trigger a deploy.
#
# Inputs (env):
#   BASE_SHA       SHA of the PR target branch tip (github.event.pull_request.base.sha)
#   HEAD_SHA       SHA of the PR head commit      (github.event.pull_request.head.sha)
#   GITHUB_OUTPUT  Path of the GitHub Actions step-output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   backend_services=<JSON array>   — service folder names under backend/*/services/
#   frontend_apps=<JSON array>      — app folder names under frontend/**/apps/**/
#   has_backend=<"true"|"false">
#   has_frontend=<"true"|"false">
#
# Backend classification rules (mirrors detect-changed-modules.sh):
#   backend/pom.xml changed            → all services
#   backend/<squad>/services/<svc>/**  → <svc>
#   backend/<squad>/libs/<lib>/**      → reverse-dep lookup → consumer services
#   backend/parents/service-parent/pom.xml → all services
#
# Frontend classification rules:
#   frontend/**/apps/**/<app>/**       → <app>
#   frontend/**/package.json           → all frontend apps (shared dep change)
#
# Exit codes:
#   0    success (arrays may be empty — that is not an error)
#   1    internal error (git or jq failure)

set -euo pipefail

: "${BASE_SHA:?BASE_SHA must be set}"
: "${HEAD_SHA:?HEAD_SHA must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REVERSE_DEPS="$SCRIPT_DIR/reverse-deps.sh"

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

# Merge the merge-base so we always diff against the common ancestor rather
# than the raw base tip (avoids false positives from commits on main that
# landed after the PR was opened).
MERGE_BASE=$(git merge-base "$BASE_SHA" "$HEAD_SHA")
CHANGED_FILES=$(git diff --name-only "$MERGE_BASE" "$HEAD_SHA")

# ---------------------------------------------------------------------------
# Backend — discover all services for "rebuild everything" fallback
# ---------------------------------------------------------------------------

ALL_SERVICES=$(find backend/*/services -maxdepth 2 -name pom.xml -exec dirname {} \; \
  | xargs -I{} basename {} \
  | jq -R -s -c 'split("\n") | map(select(length > 0))')

# ---------------------------------------------------------------------------
# Backend — detect changed services
# ---------------------------------------------------------------------------

BACKEND_SERVICES='[]'

# backend/pom.xml or service-parent → rebuild all services
if grep -qxE 'backend/pom\.xml|backend/parents/service-parent/pom\.xml' <<<"$CHANGED_FILES"; then
  BACKEND_SERVICES="$ALL_SERVICES"
else
  # Direct service changes: backend/<squad>/services/<svc>/...
  DIRECT=$(
    { grep -oE '^backend/[^/]+/services/[^/]+/' <<<"$CHANGED_FILES" || true; } \
    | sed 's:^backend/[^/]*/services/::; s:/$::' \
    | sort -u \
    | jq -R -s -c 'split("\n") | map(select(length > 0))'
  )
  BACKEND_SERVICES=$(jq -c -s 'add | unique' <<<"$BACKEND_SERVICES $DIRECT")

  # Library changes → reverse-dep lookup → add consumer services
  CHANGED_LIBS=$(
    { grep -oE '^backend/[^/]+/libs/[^/]+/' <<<"$CHANGED_FILES" || true; } \
    | sed 's:^backend/[^/]*/libs/::; s:/$::' \
    | sort -u \
    | jq -R -s -c 'split("\n") | map(select(length > 0))'
  )

  for LIB in $(jq -r '.[]' <<<"$CHANGED_LIBS"); do
    POM=$(find backend/*/libs/"$LIB"/pom.xml 2>/dev/null | head -1 || true)
    if [[ -z "$POM" || ! -f "$POM" ]]; then
      continue
    fi
    GROUP=$(sed -n '/<parent>/,/<\/parent>/d; s/.*<groupId>\(.*\)<\/groupId>.*/\1/p' "$POM" \
      | head -1 | tr -d '[:space:]' || true)
    ARTIFACT=$(sed -n '/<parent>/,/<\/parent>/d; s/.*<artifactId>\(.*\)<\/artifactId>.*/\1/p' "$POM" \
      | head -1 | tr -d '[:space:]' || true)
    if [[ -z "$GROUP" || -z "$ARTIFACT" ]]; then
      echo "::warning::Could not read coordinates from $POM — skipping reverse-dep lookup"
      continue
    fi

    rc=0
    if command -v timeout &>/dev/null; then
      REV=$(timeout 60 "$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
    elif command -v gtimeout &>/dev/null; then
      REV=$(gtimeout 60 "$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
    else
      REV=$("$REVERSE_DEPS" "$GROUP" "$ARTIFACT") || rc=$?
    fi

    if (( rc != 0 )); then
      echo "::warning::reverse-deps.sh failed (exit $rc) for $GROUP:$ARTIFACT — skipping"
      continue
    fi
    BACKEND_SERVICES=$(jq -c -s 'add | unique' <<<"$BACKEND_SERVICES $REV")
  done
fi

# ---------------------------------------------------------------------------
# Frontend — discover all deployable apps
# ---------------------------------------------------------------------------

# Apps live at: frontend/**/apps/**/<app-name>/
# The known deployable set is discovered from the directory tree rather than
# being hard-coded, so new apps are picked up automatically.
ALL_FRONTEND_APPS=$(
  find frontend -type d -path '*/apps/*/*' -prune 2>/dev/null \
  | xargs -I{} basename {} \
  | sort -u \
  | jq -R -s -c 'split("\n") | map(select(length > 0))'
)

# ---------------------------------------------------------------------------
# Frontend — detect changed apps
# ---------------------------------------------------------------------------

FRONTEND_APPS='[]'

# Root-level frontend changes (shared config, package.json, yarn.lock, etc.)
# are treated as a full rebuild trigger for all apps.
FRONTEND_ROOT_CHANGED="false"
if grep -qE '^frontend/[^/]+/(package\.json|yarn\.lock|lerna\.json|nx\.json|tsconfig)' \
      <<<"$CHANGED_FILES"; then
  FRONTEND_ROOT_CHANGED="true"
fi

if [[ "$FRONTEND_ROOT_CHANGED" == "true" ]]; then
  FRONTEND_APPS="$ALL_FRONTEND_APPS"
else
  # Per-app changes: frontend/**/apps/**/<app>/...
  DIRECT_FE=$(
    { grep -oE '^frontend/[^/]+/apps/[^/]+/[^/]+/' <<<"$CHANGED_FILES" || true; } \
    | sed 's:^.*/apps/[^/]*/::; s:/$::' \
    | sort -u \
    | jq -R -s -c 'split("\n") | map(select(length > 0))'
  )
  FRONTEND_APPS=$(jq -c -s 'add | unique' <<<"$FRONTEND_APPS $DIRECT_FE")
fi

# ---------------------------------------------------------------------------
# Derive boolean flags
# ---------------------------------------------------------------------------

HAS_BACKEND=$(jq 'length > 0' <<<"$BACKEND_SERVICES")
HAS_FRONTEND=$(jq 'length > 0' <<<"$FRONTEND_APPS")

# ---------------------------------------------------------------------------
# Write outputs
# ---------------------------------------------------------------------------

{
  echo "backend_services=$BACKEND_SERVICES"
  echo "frontend_apps=$FRONTEND_APPS"
  echo "has_backend=$HAS_BACKEND"
  echo "has_frontend=$HAS_FRONTEND"
} >> "$GITHUB_OUTPUT"

echo "Changed backend services : $BACKEND_SERVICES"
echo "Changed frontend apps    : $FRONTEND_APPS"
