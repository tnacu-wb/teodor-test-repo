#!/usr/bin/env bash
# generate-changelog.sh
#
# Renders the change log for one version of the main-branch versioning workflow
# and writes it to NOTES_FILE. The workflow uses that file as both the annotated
# tag message and the body of the GitHub Release for the tag; there is no
# CHANGELOG.md in the repo (Requirement 5).
#
# This is a thin wrapper around the maintained `conventional-changelog-cli` tool
# (conventionalcommits preset). The third-party tool is NOT reimplemented — this
# script only supplies the version heading (already computed upstream), pins the
# commit range, and writes the rendered entry to the notes file.
#
# Ordering contract: the next version is computed BEFORE the change log, because
# the entry is headed by that version (Requirement 5.7). This script therefore
# REQUIRES the version to be supplied by the caller; it never computes or guesses
# a version of its own.
#
# Commit range: the entry covers (PREVIOUS_TAG, TO_REF]. The range is passed to
# the tool explicitly through a config file (gitRawCommitsOpts.from/to) instead
# of letting it pick its own "latest tag", so the change log always covers the
# same commits the workflow versioned. PREVIOUS_TAG must be an ancestor of
# TO_REF; the workflow passes the highest X.Y.Z tag in the commit's history.
#
# First version: when PREVIOUS_TAG is empty there is no lower bound, and the tool
# would render the repo's entire history. The script writes a short
# "first versioned change log" entry instead and does not run the tool.
#
# ------------------------------------------------------------------------------
# Pinned tool version
# ------------------------------------------------------------------------------
# The CLI version is pinned to an EXACT version via the CHANGELOG_CLI_VERSION env
# var, defaulting to the constant below. Consistent with the monorepo's "no
# version ranges" convention (frontend/backend deps are pinned exactly), never
# use a caret/tilde range here. The tool is fetched on demand with
#   npx --yes --package=conventional-changelog-cli@<exact> conventional-changelog ...
# so no root package.json is required. The `conventionalcommits` preset ships
# with the CLI package (no separate install). The workflow must run
# actions/setup-node first to provide npx.
#
# Inputs (env):
#   VERSION            The already-computed semantic version (X.Y.Z) used as the
#                      entry heading. CHANGELOG_VERSION is accepted as an alias.
#   CHANGELOG_VERSION  Alias for VERSION (either may be set).
#   PREVIOUS_TAG       Exclusive lower bound of the range. Empty means this is
#                      the first version.
#   TO_REF             Inclusive upper bound of the range (default: HEAD).
#   NOTES_FILE         Path to write the rendered change log to (default:
#                      changelog.md in the current working directory).
#   CHANGELOG_CLI_VERSION  Exact conventional-changelog-cli version to run
#                          (default: DEFAULT_CHANGELOG_CLI_VERSION below).
#   CHANGELOG_PRESET   Conventional-changelog preset (default: conventionalcommits).
#
# Outputs:
#   Writes NOTES_FILE (overwriting it). When GITHUB_OUTPUT is set, writes:
#     notes-file=<path>
#
# Exit status:
#   0   change log generated
#   !=0 usage/environment error, or the CLI failed

set -euo pipefail

# Exact-pinned tool version — single source of truth (no ranges).
DEFAULT_CHANGELOG_CLI_VERSION="5.0.0"

version="${VERSION:-${CHANGELOG_VERSION:-}}"
previous_tag="${PREVIOUS_TAG:-}"
to_ref="${TO_REF:-HEAD}"
notes_file="${NOTES_FILE:-changelog.md}"
cli_version="${CHANGELOG_CLI_VERSION:-$DEFAULT_CHANGELOG_CLI_VERSION}"
preset="${CHANGELOG_PRESET:-conventionalcommits}"

semver_re='^[0-9]+\.[0-9]+\.[0-9]+$'

if [[ -z "$version" ]]; then
  echo "generate-changelog.sh: VERSION (or CHANGELOG_VERSION) must be set to the computed version" >&2
  echo "generate-changelog.sh: the change log is headed by the version, so it cannot be generated before the version is known (Requirement 5.7)." >&2
  exit 2
fi

if ! [[ "$version" =~ $semver_re ]]; then
  echo "generate-changelog.sh: VERSION is malformed: '$version'" >&2
  echo "generate-changelog.sh: expected a single semantic version matching X.Y.Z (e.g. 1.3.0)." >&2
  exit 2
fi

today="$(date +%Y-%m-%d)"

if [[ -z "$previous_tag" ]]; then
  echo "generate-changelog.sh: no previous tag; writing a first-version entry for ${version} instead of the full history"
  printf '## %s (%s)\n\nFirst versioned change log of the monorepo.\n' "$version" "$today" > "$notes_file"
else
  if ! git merge-base --is-ancestor "$previous_tag" "$to_ref" 2>/dev/null; then
    echo "::error::generate-changelog.sh: PREVIOUS_TAG '${previous_tag}' is not an ancestor of ${to_ref}; refusing to render a change log over an unrelated range." >&2
    exit 2
  fi

  if ! command -v npx >/dev/null 2>&1; then
    echo "generate-changelog.sh: npx not found on PATH; a Node.js toolchain (Node 18+) is required." >&2
    echo "generate-changelog.sh: the workflow must run actions/setup-node before this step." >&2
    exit 2
  fi

  echo "generate-changelog.sh: generating change log for ${version} over range (${previous_tag}, ${to_ref}]"
  echo "generate-changelog.sh: using conventional-changelog-cli@${cli_version} (preset: ${preset})"

  work_dir="$(mktemp -d)"
  context_file="${work_dir}/context.json"
  config_file="${work_dir}/config.cjs"
  cleanup() { rm -rf "$work_dir"; }
  trap cleanup EXIT

  # The conventionalcommits writer heads the entry with context.version. Supplying
  # it here decouples the heading from any package.json (the monorepo root has
  # none) and guarantees the heading is the already-computed version (Req 5.3).
  printf '{"version":"%s"}\n' "$version" > "$context_file"

  # Pin the git range to exactly (previous tag, TO_REF]. The values are read
  # from the environment rather than interpolated into the JavaScript.
  cat > "$config_file" <<'EOF'
module.exports = {
  gitRawCommitsOpts: {
    from: process.env.CHANGELOG_FROM,
    to: process.env.CHANGELOG_TO,
  },
};
EOF

  # -r 1 renders a single entry. The conventionalcommits preset groups commits
  # by type (feat -> Features, fix -> Bug Fixes, ...) and includes the date in
  # the heading (Requirements 5.2, 5.3).
  set +e
  CHANGELOG_FROM="$previous_tag" CHANGELOG_TO="$to_ref" \
    npx --yes --package="conventional-changelog-cli@${cli_version}" \
    conventional-changelog \
    --preset "$preset" \
    --config "$config_file" \
    --context "$context_file" \
    --release-count 1 \
    --outfile "$notes_file"
  cli_status=$?
  set -e

  if [[ "$cli_status" -ne 0 ]]; then
    echo "::error::generate-changelog.sh: conventional-changelog-cli failed with exit status ${cli_status}" >&2
    exit "$cli_status"
  fi

  if [[ ! -s "$notes_file" ]]; then
    echo "generate-changelog.sh: no change log was produced for ${version}; this usually means no conventional commits were found in the range. Writing a minimal dated heading so the tag still has a message (Req 5.6)." >&2
    printf '## %s (%s)\n' "$version" "$today" > "$notes_file"
  fi
fi

echo "generate-changelog.sh: wrote change log for ${version} to ${notes_file}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "notes-file=${notes_file}" >> "$GITHUB_OUTPUT"
fi
