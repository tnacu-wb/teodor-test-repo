#!/usr/bin/env bash
# resolve-image-tag.sh
#
# Generates Docker image tags for PR (ephemeral) and main-line builds.
#
# For pull_request events:
#   - Uses github.sha, which points to GitHub's test merge commit (PR head
#     merged into the current tip of the target branch). This ensures ephemeral
#     environments test the actual merge result, catching integration issues early.
#   - Format: pr-<number>-<merge-sha7>
#
# For push events (main, release/*, hotfix/*):
#   - When SEMANTIC_VERSION is supplied (merge-to-main versioning flow) and is a
#     well-formed X.Y.Z, the tag is that version verbatim (e.g. "1.3.0"), with
#     no ref/sha/timestamp appended (Requirements 2.5, 10.1).
#   - Otherwise, uses the frontend tagging pattern for consistency across the monorepo.
#   - Format: <app-version>-<ref-name>-<full-sha>-<timestamp>
#   - Example: 1.0.0-main-abc1234567890abcdef1234567890abcdef1234-1709567890
#
# Inputs (env):
#   EVENT_NAME      github.event_name (pull_request or push)
#   PR_NUMBER       Pull request number (required for PR path)
#   COMMIT_SHA      github.sha (merge commit for PRs, actual commit for pushes)
#   BRANCH_NAME     Branch name (for main-line path)
#   APP_VERSION     Application version (for main-line path, default: 1.0.0)
#   SEMANTIC_VERSION  Repo-wide semantic version from the main-branch versioning
#                     flow (main-line path only). When set and well-formed, it
#                     is emitted verbatim as the image tag.
#   GITHUB_OUTPUT   Path to GitHub Actions output file
#
# Outputs (written to $GITHUB_OUTPUT):
#   tag=<image-tag>
#
set -euo pipefail

: "${EVENT_NAME:?EVENT_NAME must be set}"
: "${COMMIT_SHA:?COMMIT_SHA must be set}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

if [[ "$EVENT_NAME" == "pull_request" || -n "${PR_NUMBER:-}" ]]; then
  # Ephemeral image tag: pr-<number>-<merge-sha>
  # This represents the test merge of the PR into the current target branch.
  : "${PR_NUMBER:?PR_NUMBER must be set for pull_request events}"
  tag="pr-${PR_NUMBER}-${COMMIT_SHA:0:7}"
  echo "Image tag (ephemeral): $tag"
elif [[ -n "${SEMANTIC_VERSION:-}" && "$SEMANTIC_VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  # Main-branch versioning path: the versioning flow computed a repo-wide
  # semantic version and tagged the commit with it. Emit that version
  # verbatim as the image tag — no ref/sha/timestamp appended (Reqs 2.5, 10.1).
  tag="$SEMANTIC_VERSION"
  echo "Image tag (semantic version): $tag"
else
  # Main-line image tag: <app-version>-<ref-name>-<full-sha>-<timestamp>
  # Matches the frontend tagging pattern for consistency.
  : "${BRANCH_NAME:?BRANCH_NAME must be set for push events}"
  
  APP_VERSION="${APP_VERSION:-1.0.0}"
  TIMESTAMP=$(date +%s)
  
  # Normalize branch name (replace slashes with hyphens)
  case "$BRANCH_NAME" in
    main)
      TAG_REF="main"
      ;;
    *)
      TAG_REF="${BRANCH_NAME//\//-}"
      ;;
  esac
  
  tag="${APP_VERSION}-${TAG_REF}-${COMMIT_SHA}-${TIMESTAMP}"
  echo "Image tag (main-line): $tag"
fi

echo "tag=$tag" >> "$GITHUB_OUTPUT"
