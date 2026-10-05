#!/usr/bin/env bash
# compute-next-version.sh
#
# Computes the next repo-wide semantic version for the main-branch versioning
# workflow (Requirements 2.2, 2.3, 3.1-3.8).
#
# Algorithm:
#   1. If CURRENT_VERSION is empty/absent -> candidate = INITIAL_VERSION.
#      Else validate against ^[0-9]+\.[0-9]+\.[0-9]+$ and minor-bump with patch
#      reset: X.Y.Z -> X.(Y+1).0.
#   2. Collision avoidance: while candidate is in EXISTING_TAGS, bump minor again.
#   3. Emit the chosen candidate.
#
# A malformed CURRENT_VERSION is a hard failure with an actionable message
# (no silent reset to 1.0.0).
#
# Inputs (env):
#   CURRENT_VERSION  Highest existing X.Y.Z tag (the version lives only in tags);
#                    empty/absent => first version, initialise
#   EXISTING_TAGS    Newline-separated list of existing version tag names
#   INITIAL_VERSION  Initialisation value (default: 1.0.0; the workflow sets 1.3.0)
#   GITHUB_OUTPUT    Path to GitHub Actions output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   version=<X.Y.Z>
#
# STATUS: implemented (task 2.1).

set -euo pipefail

semver_re='^[0-9]+\.[0-9]+\.[0-9]+$'

current_version="${CURRENT_VERSION:-}"
existing_tags="${EXISTING_TAGS:-}"
initial_version="${INITIAL_VERSION:-1.0.0}"

# Trim surrounding whitespace/newlines the caller's value may carry.
current_version="${current_version#"${current_version%%[![:space:]]*}"}"
current_version="${current_version%"${current_version##*[![:space:]]}"}"

if ! [[ "$initial_version" =~ $semver_re ]]; then
  echo "compute-next-version.sh: INITIAL_VERSION must match X.Y.Z (got '$initial_version')" >&2
  exit 1
fi

# Bump the minor component and reset patch to 0: X.Y.Z -> X.(Y+1).0.
bump_minor() {
  local version="$1"
  local major minor
  major="${version%%.*}"
  minor="${version#*.}"
  minor="${minor%%.*}"
  # 10# forces base-10 so a leading zero (e.g. 08) is not parsed as octal.
  echo "${major}.$(( 10#$minor + 1 )).0"
}

# Determine the initial candidate.
if [[ -z "$current_version" ]]; then
  # Empty/absent current version => initialise (Requirement 2.3).
  candidate="$initial_version"
else
  if ! [[ "$current_version" =~ $semver_re ]]; then
    echo "compute-next-version.sh: CURRENT_VERSION is malformed: '$current_version'" >&2
    echo "compute-next-version.sh: expected a single semantic version matching X.Y.Z (e.g. 1.3.0)." >&2
    echo "compute-next-version.sh: refusing to reset the version silently; fix the offending tag and retry." >&2
    exit 1
  fi
  # Minor bump with patch reset (Requirements 3.1-3.5).
  candidate="$(bump_minor "$current_version")"
fi

# Collision avoidance: while the candidate already exists as a tag, bump again
# (Requirement 3.8). EXISTING_TAGS is a newline-separated list.
tag_exists() {
  local target="$1"
  local tag
  while IFS= read -r tag; do
    # Trim whitespace from each tag entry.
    tag="${tag#"${tag%%[![:space:]]*}"}"
    tag="${tag%"${tag##*[![:space:]]}"}"
    [[ -z "$tag" ]] && continue
    if [[ "$tag" == "$target" ]]; then
      return 0
    fi
  done <<< "$existing_tags"
  return 1
}

while tag_exists "$candidate"; do
  echo "compute-next-version.sh: candidate ${candidate} already exists as a tag; bumping minor again" >&2
  candidate="$(bump_minor "$candidate")"
done

echo "compute-next-version.sh: selected version ${candidate}"

if [[ -n "${GITHUB_OUTPUT:-}" ]]; then
  echo "version=${candidate}" >> "$GITHUB_OUTPUT"
else
  echo "compute-next-version.sh: GITHUB_OUTPUT not set; printing result to stdout" >&2
  echo "version=${candidate}"
fi
