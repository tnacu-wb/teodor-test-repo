#!/usr/bin/env bash
# Posts the PR link and the "Steps to Replicate / Verify" section to the linked issue
# after an ephemeral preview deploys. Idempotent across the per-app deploy matrix via a
# hidden marker.
#
# Invoked from the reusable deploy workflow (deploy job checks out source):
#   run: bash "$CI_SCRIPT_ROOT/notifications/post-issue-verification.sh"
#
# Required env (passed via the step `env:` block):
#   ISSUE_NUMBER - linked issue number derived by deployment-identity
#   PR_NUMBER    - PR number that triggered the deploy
#   REPO         - owner/repo (github.repository)
#   GH_TOKEN     - token used by the gh CLI
set -euo pipefail

if [[ -z "${ISSUE_NUMBER:-}" || "${ISSUE_NUMBER}" == "0" ]]; then
  echo "No linked issue number; skipping issue comment."
  exit 0
fi
if [[ -z "${PR_NUMBER:-}" || "${PR_NUMBER}" == "0" ]]; then
  echo "No PR number supplied; skipping issue comment."
  exit 0
fi

# The comment entry point has no pull_request in the event payload, so fetch PR details
# from the API by number.
pr_json=$(gh api "repos/${REPO}/pulls/${PR_NUMBER}")
pr_url=$(printf '%s' "$pr_json" | jq -r '.html_url // ""')
pr_body=$(printf '%s' "$pr_json" | jq -r '.body // ""')
pr_number=$(printf '%s' "$pr_json" | jq -r '.number')

# Hidden marker so we only post once across the per-app deploy matrix.
marker="<!-- kiro-issue-comment:pr-${pr_number} -->"

if gh api "repos/${REPO}/issues/${ISSUE_NUMBER}/comments" --paginate --jq '.[].body' \
  | grep -F "$marker" >/dev/null; then
  echo "Issue #${ISSUE_NUMBER} already has the PR comment; skipping."
  exit 0
fi

# Extract the "Steps to Replicate / Verify" section (case-insensitive header), stopping
# at the next level-3 heading. Trailing blank lines are trimmed.
replication_steps=$(printf '%s\n' "$pr_body" | awk '
  BEGIN { grab = 0 }
  tolower($0) ~ /^###[[:space:]]*steps to replicate/ { grab = 1; next }
  grab && /^###[[:space:]]/ { exit }
  grab { print }
' | sed -e 's/[[:space:]]*$//')
replication_steps=$(printf '%s' "$replication_steps" | sed -e :a -e '/^\n*$/{$d;N;ba}')

if [[ -n "$replication_steps" ]]; then
  steps_section=$(printf '\n\n### Steps to Replicate / Verify\n%s' "$replication_steps")
else
  steps_section=$'\n\n_No "Steps to Replicate / Verify" section was found in the PR description._'
fi

comment_body="${marker}
A pull request has been opened for this issue: ${pr_url}${steps_section}"

jq -n --arg body "$comment_body" '{ body: $body }' \
  | gh api "repos/${REPO}/issues/${ISSUE_NUMBER}/comments" -X POST --input - >/dev/null

echo "Posted PR link and replication steps to issue #${ISSUE_NUMBER}."
