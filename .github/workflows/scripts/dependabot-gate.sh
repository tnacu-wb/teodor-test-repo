#!/usr/bin/env bash
set -euo pipefail

# Dependency hygiene gate. Fails the build when dependabot has either open PRs
# or open alerts of severity medium or above.
# Counting is repo-wide: any open dependabot PR fails the gate, regardless of
# which stack it belongs to.

: "${GH_TOKEN:?GH_TOKEN must be set}"
: "${REPO:?REPO must be set (owner/name)}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

if ! opened_prs=$(gh api -X GET search/issues \
  -f q="repo:${REPO} is:pr state:open label:dependencies author:app/dependabot" \
  -q '.total_count' 2>&1) || [[ ! "$opened_prs" =~ ^[0-9]+$ ]]; then
  echo "::error::Could not read open dependabot PRs for ${REPO}. Refusing to pass the" \
    "gate on an unknown count."
  printf '%s\n' "$opened_prs"
  exit 1
fi

# --paginate so the count is not capped at one page. An unreadable alerts API is
# a hard failure, not a zero: a missing permission must not look like a clean repo.
if ! alerts_raw=$(gh api --paginate \
  "repos/${REPO}/dependabot/alerts?state=open&severity=medium,high,critical&per_page=100" \
  -q 'length' 2>&1); then
  echo "::error::Could not read dependabot alerts for ${REPO}. The token needs read" \
    "access to Dependabot alerts. Refusing to pass the gate on an unknown count."
  printf '%s\n' "$alerts_raw"
  exit 1
fi
dependabot_cves=$(printf '%s\n' "$alerts_raw" | awk '{s+=$1} END {print s+0}')

echo "opened-prs=${opened_prs}" >> "$GITHUB_OUTPUT"
echo "dependabot-cves=${dependabot_cves}" >> "$GITHUB_OUTPUT"

echo "Open dependabot PRs (label: dependencies): ${opened_prs}"
echo "Open dependabot alerts (medium+):          ${dependabot_cves}"

if [[ "$opened_prs" -eq 0 && "$dependabot_cves" -eq 0 ]]; then
  echo "Dependency gate passed."
  echo "should-comment=false" >> "$GITHUB_OUTPUT"
  exit 0
fi

{
  echo "**dependabot-scan:**"
  echo ""
  if [[ "$opened_prs" -ne 0 ]]; then
    echo "There are ${opened_prs} open dependabot PR(s). These need to be merged!"
    echo ""
  fi
  if [[ "$dependabot_cves" -ne 0 ]]; then
    echo "There are ${dependabot_cves} CVE(s) alerted by dependabot. These need to be fixed!"
  fi
} > dependabot-gate-comment.md

echo "should-comment=true" >> "$GITHUB_OUTPUT"
echo "::error::Dependency gate failed - ${opened_prs} open dependabot PR(s), ${dependabot_cves} open medium+ alert(s)."
exit 1
