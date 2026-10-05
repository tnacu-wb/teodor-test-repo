#!/usr/bin/env bash
# detect-changed-python-tools.sh
#
# Discover which Python tools under tools/python/<name>/ changed between two
# commits, and emit a matrix that drives ci-python-tools.yaml. Nothing about
# any specific tool is hard-coded here — a new tool under tools/python/ is
# picked up automatically with no workflow edit.
#
# Inputs (env):
#   BASE_SHA       Base commit of the diff range (optional). If unset/empty,
#                  all-zero, or not a resolvable commit, every tool under
#                  tools/python/ is selected (full rebuild — used for
#                  workflow_dispatch and a branch's first push).
#   HEAD_SHA       Head commit of the diff range (optional; defaults to HEAD).
#   GITHUB_OUTPUT  GitHub Actions step-output file (optional; falls back to stdout).
#
# Output (written to $GITHUB_OUTPUT, or stdout if unset):
#   tools=<JSON array of {name, path} objects>   (single line; may be [])
#
# For each selected tool it derives, from the tool's own files:
#   name           the tool directory name
#   path           tools/python/<name>
# Per-tool details that depend on file contents (python version, image name,
# whether a Dockerfile exists) are resolved in the workflow job itself, so this
# script stays a pure change-detector.
#
# Exit codes: 0 success (including empty result).

set -euo pipefail

ROOT="tools/python"
HEAD_SHA="${HEAD_SHA:-HEAD}"

# All tools currently present under tools/python/ (a dir is a "tool" if it has
# a pyproject.toml).
all_tools() {
  find "$ROOT" -mindepth 2 -maxdepth 2 -name pyproject.toml -exec dirname {} \; 2>/dev/null \
    | sort -u
}

# Treat an unset, empty, or all-zero BASE_SHA (github.event.before on a branch's
# first push), or a base that doesn't resolve to a commit, as "no usable base"
# → select every tool (safe full rebuild) rather than failing.
if [[ -n "${BASE_SHA:-}" && ! "$BASE_SHA" =~ ^0+$ ]] && git rev-parse --verify --quiet "${BASE_SHA}^{commit}" >/dev/null; then
  BASE_SHA="$(git merge-base "$BASE_SHA" "$HEAD_SHA")"
  CHANGED_FILES="$(git diff --name-only "$BASE_SHA" "$HEAD_SHA")"
  # Reduce changed files to the set of tools/python/<name> dirs they touch.
  CHANGED_TOOL_DIRS="$(grep -oE "^${ROOT}/[^/]+/" <<<"$CHANGED_FILES" | sed 's:/$::' | sort -u || true)"
  # Keep only dirs that still exist with a pyproject.toml (ignore deletions).
  SELECTED=""
  for d in $CHANGED_TOOL_DIRS; do
    [[ -f "$d/pyproject.toml" ]] && SELECTED+="$d"$'\n'
  done
else
  # No usable base — select every tool present (safe full rebuild). This covers
  # workflow_dispatch (BASE_SHA empty) and a branch's first push (before is
  # all-zero). A normal push DOES supply github.event.before, so it takes the
  # diff path above, not this branch.
  SELECTED="$(all_tools)"
fi

# Build the JSON matrix: [{"name":"<dir>","path":"tools/python/<dir>"}, ...]
MATRIX="$(printf '%s\n' "$SELECTED" \
  | sed '/^$/d' \
  | jq -R -s -c 'split("\n") | map(select(length>0)) | map({name: (. | split("/") | last), path: .})')"

: "${MATRIX:=[]}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "tools=$MATRIX" >> "$GITHUB_OUTPUT"
else
  echo "tools=$MATRIX"
fi
