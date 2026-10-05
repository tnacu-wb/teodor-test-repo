#!/usr/bin/env bash
# Registers a PR-head-bound GitHub Deployment for a comment-triggered ephemeral
# preview, then marks it successful so it links to the PR's Deployments box.
#
# Invoked from the reusable deploy workflow (deploy job checks out source):
#   run: bash "$CI_SCRIPT_ROOT/deploy/register-pr-deployment.sh"
#
# Required env (passed via the step `env:` block):
#   APP_NAME     - matrix app name (business-booker | ccui | premier-inn)
#   PR_NUMBER    - PR number driving the <app>-PR-<n> environment name
#   CHECKOUT_REF - immutable PR head SHA the deployment binds to
#   HOST_NAME    - preview host used for the environment URL
#   REPO         - owner/repo (github.repository)
#   GH_TOKEN     - token used by the gh CLI
set -euo pipefail

environment="${APP_NAME}-PR-${PR_NUMBER}"

deployment_payload=$(jq -n \
  --arg ref "$CHECKOUT_REF" \
  --arg env "$environment" \
  '{
    ref: $ref,
    environment: $env,
    auto_merge: false,
    required_contexts: [],
    transient_environment: true,
    production_environment: false,
    description: "Ephemeral frontend preview (comment-triggered)"
  }')

deployment_id=$(printf '%s' "$deployment_payload" \
  | gh api "repos/${REPO}/deployments" -X POST --input - --jq '.id // empty')

if [[ -z "$deployment_id" ]]; then
  echo "::warning::Could not create deployment for ${environment}."
  exit 0
fi

jq -n \
  --arg env "$environment" \
  --arg url "https://${HOST_NAME}" \
  '{
    state: "success",
    environment: $env,
    environment_url: $url,
    description: "Preview deployed"
  }' \
  | gh api "repos/${REPO}/deployments/${deployment_id}/statuses" -X POST --input - >/dev/null

echo "Registered PR-scoped deployment ${deployment_id} for ${environment} at ${CHECKOUT_REF}."
