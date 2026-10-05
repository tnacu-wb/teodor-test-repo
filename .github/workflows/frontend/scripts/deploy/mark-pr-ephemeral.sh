#!/usr/bin/env bash
# Adds the 'ephemeral-active' label to a PR so later commits redeploy the ephemeral
# environment. Creates the repo label on first use, then retries once.
#
# Invoked from the reusable deploy workflow (mark-pr-ephemeral job sparse-checks-out the
# scripts directory first):
#   run: bash "$CI_SCRIPT_ROOT/deploy/mark-pr-ephemeral.sh"
#
# Required env (passed via the step `env:` block):
#   PR_NUMBER - PR number to label
#   REPO      - owner/repo (github.repository)
#   GH_TOKEN  - token used by the gh CLI
set -euo pipefail

if [[ ! "${PR_NUMBER:-}" =~ ^[0-9]+$ ]] || [[ "${PR_NUMBER}" -le 0 ]]; then
  echo "No valid PR number supplied; skipping label."
  exit 0
fi

label="ephemeral-active"

add_label() {
  jq -n --arg l "$label" '{ labels: [$l] }' \
    | gh api "repos/${REPO}/issues/${PR_NUMBER}/labels" -X POST --input - >/dev/null
}

if add_label 2>/dev/null; then
  echo "Labelled PR #${PR_NUMBER} with '${label}'."
  exit 0
fi

# First-ever use: the repo label may not exist. Create it (ignore failure), then retry.
jq -n --arg n "$label" \
  '{ name: $n, color: "0e8a16", description: "PR has a frontend ephemeral environment; redeploy on new commits." }' \
  | gh api "repos/${REPO}/labels" -X POST --input - >/dev/null 2>&1 || true

add_label
echo "Created label '${label}' and applied it to PR #${PR_NUMBER}."
