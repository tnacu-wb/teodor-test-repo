#!/usr/bin/env bash
#
# detect-charts.sh — decide which chart(s) the Helm Deploy workflow should deploy.
#
# Consumed by .github/workflows/helm-deploy.yml. All inputs arrive as environment
# variables (never interpolated into the script body), so untrusted values —
# the workflow_dispatch chart input and git-derived directory names — cannot
# inject shell.
#
# Inputs (env):
#   EVENT_NAME   - GitHub event name ("workflow_dispatch" or "push").
#   INPUT_CHART  - dispatch only: chart path relative to infra/ (e.g. "temporal"
#                  or "helm/opera-app-chart").
#   BEFORE_SHA   - push only: the "before" commit of the push range.
#   AFTER_SHA    - push only: the "after"/head commit of the push range.
#   INFRA_DIR    - path to the infra/ directory (default: "infra").
#   GITHUB_OUTPUT- (optional) if set, "charts=<json>" is appended to it.
#
# Output:
#   Prints "charts=<json-array>" to stdout, and appends the same to
#   $GITHUB_OUTPUT when set. The array holds chart paths relative to infra/.
#
# A "chart" is a directory containing a Chart.yaml. A directory containing a
# ".helm-deploy-ignore" file is a base/library chart and is skipped.
#
set -euo pipefail

INFRA_DIR="${INFRA_DIR:-infra}"

log() { echo "$@" >&2; }
die() { echo "::error::$*" >&2; exit 1; }

# A chart path must be a single-line relative path of safe characters only:
# no newlines (which could forge extra $GITHUB_OUTPUT lines), no leading slash
# (absolute), no "..", and only [A-Za-z0-9._/-]. Rejects on any violation.
validate_chart_path() {
  local p="$1"
  case "$p" in
    ""|/*|*..*|*[!A-Za-z0-9._/-]*)
      return 1 ;;
  esac
  # Reject a trailing slash and any control chars/newlines that the glob above
  # might not catch on some shells.
  case "$p" in
    */) return 1 ;;
  esac
  # Reject a "." path component (e.g. ".", "./x", "x/.", "a/./b"). It resolves to
  # a real dir, so it would pass resolution, but basename would then yield "."
  # downstream and break namespace/release derivation.
  case "/$p/" in
    */./*) return 1 ;;
  esac
  return 0
}

# True if the chart dir opts out of standalone deployment (base/library chart).
is_ignored_chart() {
  [ -f "${INFRA_DIR}/$1/.helm-deploy-ignore" ]
}

# Confirm infra/<chart> resolves to a real directory strictly beneath infra/
# (blocks ../-escapes) and actually contains a Chart.yaml.
resolve_and_check_under_infra() {
  local rel="$1" infra_root candidate
  infra_root="$(cd "$INFRA_DIR" && pwd -P)"
  # Resolve the candidate's real path; empty if it doesn't exist. Use an if so
  # a resolution failure isn't masked by an ambiguous && ... || chain (SC2015).
  if ! candidate="$(cd "${INFRA_DIR}/${rel}" 2>/dev/null && pwd -P)"; then
    return 1
  fi
  [ -n "$candidate" ] || return 1
  case "$candidate" in
    "$infra_root"/*) : ;;
    *) return 1 ;;
  esac
  [ -f "${INFRA_DIR}/${rel}/Chart.yaml" ]
}

# Map an arbitrary changed-file path to the nearest ancestor dir (relative to
# infra/) that contains a Chart.yaml. Prints the chart path, or nothing.
chart_for_path() {
  local p="$1" dir
  dir="$(dirname "$p")"
  while [ "$dir" != "." ] && [ "$dir" != "/" ]; do
    if [ -f "${dir}/Chart.yaml" ]; then
      echo "${dir#"${INFRA_DIR}"/}"
      return 0
    fi
    dir="$(dirname "$dir")"
  done
  return 0
}

emit() {
  local charts_json="$1"
  log "Charts to deploy: $charts_json"
  echo "charts=$charts_json"
  if [ -n "${GITHUB_OUTPUT:-}" ]; then
    echo "charts=$charts_json" >> "$GITHUB_OUTPUT"
  fi
}

main() {
  local charts_json
  if [ "${EVENT_NAME:-}" = "workflow_dispatch" ]; then
    local chart="${INPUT_CHART:-}"
    validate_chart_path "$chart" \
      || die "Invalid chart input '${chart}': must be a relative path under infra/ using only [A-Za-z0-9._/-], no '..', no trailing slash."
    resolve_and_check_under_infra "$chart" \
      || die "Chart path '${chart}' does not resolve to a chart directory under infra/."
    if is_ignored_chart "$chart"; then
      die "Chart '${chart}' is a base/library chart (.helm-deploy-ignore present) and is not independently deployable."
    fi
    charts_json="$(jq -n -c --arg c "$chart" '[$c]')"
    emit "$charts_json"
    return 0
  fi

  # push: compute charts from the changed files in the push range.
  [ -n "${BEFORE_SHA:-}" ] || die "BEFORE_SHA is required for push events."
  [ -n "${AFTER_SHA:-}" ]  || die "AFTER_SHA is required for push events."

  # On a branch-creation push GitHub sets github.event.before to the all-zero
  # SHA, which no fetch can resolve. Diff against Git's empty tree in that case
  # so every file in the after tree counts as "changed" (this normally only
  # applies to branch pushes, not the main-branch trigger this workflow uses).
  local base
  if printf '%s' "$BEFORE_SHA" | grep -qE '^0{40}$|^0{64}$'; then
    base="$(git hash-object -t tree /dev/null)"  # the empty-tree object
    log "Base is the zero SHA (branch creation) — diffing against the empty tree."
  else
    git rev-parse --verify --quiet "${BEFORE_SHA}^{commit}" >/dev/null \
      || die "Base commit ${BEFORE_SHA} not found in history — cannot compute changed charts (need full fetch)."
    base="$BEFORE_SHA"
  fi

  local changed
  # Use a path-prefix pathspec ("infra/") rather than a glob ("infra/**"): git
  # pathspecs are not shell globs, and the prefix form unambiguously means
  # "every path under infra/" at any depth.
  changed="$(git diff --name-only "${base}" "${AFTER_SHA}" -- "${INFRA_DIR}/")"

  local found=""
  local f c
  while IFS= read -r f; do
    [ -n "$f" ] || continue
    c="$(chart_for_path "$f")"
    [ -n "$c" ] || continue
    # Defence in depth: a git path should already be safe, but validate before
    # it can reach $GITHUB_OUTPUT / downstream cd.
    if ! validate_chart_path "$c"; then
      log "Skipping chart with unsafe path: $c"
      continue
    fi
    if is_ignored_chart "$c"; then
      log "Skipping base/library chart (.helm-deploy-ignore): $c"
      continue
    fi
    found="${found}${c}"$'\n'
  done <<EOF
$changed
EOF

  if [ -z "$found" ]; then
    log "No deployable chart changes detected under ${INFRA_DIR}/ — nothing to deploy."
    emit "[]"
    return 0
  fi

  charts_json="$(printf '%s' "$found" | sort -u | jq -R -s -c 'split("\n") | map(select(length > 0))')"
  emit "$charts_json"
}

main "$@"
