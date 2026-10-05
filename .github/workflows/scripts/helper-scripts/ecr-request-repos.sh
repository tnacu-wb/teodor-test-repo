#!/usr/bin/env bash

# Script for ecr-request-repos.yaml; one function per workflow step.
# Usage: ecr-request-repos.sh <setup-yq|request|verify-plan|merge|wait-apply|revert>

set -euo pipefail

YQ_VERSION="${YQ_VERSION:-v4.53.3}"
YQ_SHA256="${YQ_SHA256:-fa52a4e758c63d38299163fbdd1edfb4c4963247918bf9c1c5d31d84789eded4}"

setup_yq() {
  local tmp
  tmp="$(mktemp)"
  curl -fsSL -o "$tmp" \
    "https://github.com/mikefarah/yq/releases/download/${YQ_VERSION}/yq_linux_amd64"
  echo "${YQ_SHA256}  ${tmp}" | sha256sum -c -
  sudo install -m 0755 "$tmp" /usr/local/bin/yq
  rm -f "$tmp"
  yq --version
}

request() {
  if [[ ! -f "$REPOS_LIST" ]]; then
    echo "::error::Repositories file '${REPOS_LIST}' not found in ${TERRAFORM_REPO}."
    exit 1
  fi

  # Parse & validate requested names
  mapfile -t REQUESTED < <(jq -r '.[]' <<<"$REPO_NAMES")
  if (( ${#REQUESTED[@]} == 0 )); then
    echo "::notice::No service names supplied; nothing to do."
    {
      echo "status=already-declared"
      echo "repository-names=[]"
    } >> "$GITHUB_OUTPUT"
    exit 0
  fi
  for name in "${REQUESTED[@]}"; do
    if [[ ! "$name" =~ ^[a-z0-9._-]+$ ]]; then
      echo "::error::Invalid repo name '${name}'. Allowed: lowercase letters, digits, and . _ -"
      exit 1
    fi
  done

  # Filter names not already declared in repositories.yaml
  echo "::group::Filter already-declared repositories"
  TO_ADD=()
  for name in "${REQUESTED[@]}"; do
    if name="$name" yq -e '.repositories[] | select(. == strenv(name))' "$REPOS_LIST" >/dev/null 2>&1; then
      echo "::notice::'${name}' already declared; skipping."
    else
      TO_ADD+=("$name")
    fi
  done
  echo "::endgroup::"

  if (( ${#TO_ADD[@]} == 0 )); then
    echo "::notice::All requested repositories already declared; nothing to do."
    {
      echo "status=already-declared"
      echo "repository-names=[]"
    } >> "$GITHUB_OUTPUT"
    exit 0
  fi

  ADD_COUNT=${#TO_ADD[@]}
  EXPECTED_ADD=$(( ADD_COUNT * PER_REPO_ADD ))
  echo "expected-add=${EXPECTED_ADD}" >> "$GITHUB_OUTPUT"
  echo "::notice::Adding ${ADD_COUNT} repositor$( ((ADD_COUNT==1)) && echo y || echo ies ); expecting ${EXPECTED_ADD} to add."

  # Deterministic branch for this exact set, so a re-run of a failed request
  # reuses the same branch/PR rather than leaving a dangling duplicate
  KEY=$(printf '%s\n' "${TO_ADD[@]}" | sort | sha1sum | cut -c1-12)
  BRANCH="auto/ecr-add-${KEY}"

  # Derive JIRA ticket from caller's commit message/branch for PR naming.
  # Matches projects that consume this workflow (CTECH, SRE).
  TICKET="$(grep -oiE '(CTECH|SRE)-[0-9]+' <<<"${HEAD_COMMIT_MSG:-} ${SOURCE_BRANCH:-}" | head -1 | tr '[:lower:]' '[:upper:]' || true)"
  SCOPE="${TICKET:-ecr}"

  LIST_MD=$(printf -- '- `%s`\n' "${TO_ADD[@]}")

  # Reuse an existing open PR for this set (failed-run recovery); otherwise create it
  echo "::group::Open or reuse request PR"
  PR_NUMBER=$(gh pr list --repo "$TERRAFORM_REPO" --head "$BRANCH" --state open --json number --jq '.[0].number // ""')
  if [[ -n "$PR_NUMBER" ]]; then
    PR_URL=$(gh pr view "$PR_NUMBER" --repo "$TERRAFORM_REPO" --json url --jq '.url')
    echo "::notice::Reusing open request PR #${PR_NUMBER}: ${PR_URL}"
  else
    git config user.name "ecr-request-bot"
    git config user.email "ecr-request-bot@users.noreply.github.com"
    git checkout -b "$BRANCH"
    for name in "${TO_ADD[@]}"; do
      name="$name" yq -i '.repositories += [strenv(name)] | .repositories |= unique' "$REPOS_LIST"
    done
    git add "$REPOS_LIST"
    # Continuation lines start at column 0 so the commit body has no stray indentation
    git commit -m "feat(${SCOPE}): add ${ADD_COUNT} ECR repo(s)

Requested automatically by ${SOURCE_REPO}:
${LIST_MD}"
    git push --force --set-upstream origin "$BRANCH"

    if PR_URL=$(gh pr create --repo "$TERRAFORM_REPO" --base "$BASE_BRANCH" --head "$BRANCH" \
      --title "feat(${SCOPE}): add ${ADD_COUNT} ECR repo(s)" \
      --body "Automated request to create ECR repositories:
${LIST_MD}
Requested by **${SOURCE_REPO}** — run: ${SOURCE_RUN_URL}

Merging triggers terragrunt apply pipeline which creates repos with standard config defined in module."); then
      echo "::notice::Opened request PR: ${PR_URL}"
    else
      PR_URL=$(gh pr list --repo "$TERRAFORM_REPO" --head "$BRANCH" --state open --json url --jq '.[0].url // ""')
      if [[ -z "$PR_URL" ]]; then
        echo "::error::gh pr create failed and no open PR exists for '${BRANCH}'; branch pushed without a PR - re-run to retry."
        exit 1
      fi
      echo "::notice::Adopted concurrently-created PR: ${PR_URL}"
    fi
    PR_NUMBER="${PR_URL##*/}"
  fi
  echo "::endgroup::"

  TO_ADD_JSON=$(printf '%s\n' "${TO_ADD[@]}" | jq -R -s -c 'split("\n") | map(select(length>0))')
  {
    echo "status=pr-opened"
    echo "repository-names=${TO_ADD_JSON}"
    echo "pr-url=${PR_URL}"
    echo "pr-number=${PR_NUMBER}"
  } >> "$GITHUB_OUTPUT"
}

verify_plan() {
  echo "::group::Wait for terragrunt plan comment on PR #${PR_NUMBER}"
  deadline=$(( SECONDS + PLAN_TIMEOUT ))
  plan_line=""
  while (( SECONDS < deadline )); do
    body="$(gh pr view "$PR_NUMBER" --repo "$TERRAFORM_REPO" --json comments \
             --jq '[.comments[] | select(.body | test("Plan: [0-9]+ to add"))] | last | .body // ""')"
    if [[ -n "$body" ]]; then
      plan_line="$(grep -oE 'Plan: [0-9]+ to add, [0-9]+ to change, [0-9]+ to destroy' <<<"$body" | tail -1)"
      [[ -n "$plan_line" ]] && break
    fi
    sleep 15
  done
  echo "::endgroup::"

  if [[ -z "$plan_line" ]]; then
    echo "::error::No terragrunt plan summary found on PR #${PR_NUMBER} within ${PLAN_TIMEOUT}s."
    exit 1
  fi
  echo "::notice::${plan_line}"

  read -r add change destroy < <(grep -oE '[0-9]+' <<<"$plan_line" | tr '\n' ' ') || true
  if [[ "$add" != "$EXPECTED_ADD" || "$change" != "0" || "$destroy" != "0" ]]; then
    echo "::error::Unexpected plan (${plan_line}). Require ${EXPECTED_ADD} to add / 0 to change / 0 to destroy."
    exit 1
  fi
  echo "::notice::Plan verified: ${EXPECTED_ADD} to add, 0 to change, 0 to destroy."
}

merge_pr() {
  gh pr merge "$PR_NUMBER" --repo "$TERRAFORM_REPO" --squash --admin --delete-branch
  MERGE_SHA="$(gh pr view "$PR_NUMBER" --repo "$TERRAFORM_REPO" --json mergeCommit --jq '.mergeCommit.oid')"
  echo "merge-sha=${MERGE_SHA}" >> "$GITHUB_OUTPUT"
  echo "::notice::Merged PR #${PR_NUMBER} as ${MERGE_SHA}."
}

wait_apply() {
  apply_lc="$(tr '[:upper:]' '[:lower:]' <<<"$APPLY_WORKFLOW")"
  echo "::group::Wait for '${APPLY_WORKFLOW}' apply run on ${MERGE_SHA}"
  deadline=$(( SECONDS + APPLY_TIMEOUT ))
  status="" conclusion=""
  while (( SECONDS < deadline )); do
    run_json="$(gh api "repos/${TERRAFORM_REPO}/actions/runs?head_sha=${MERGE_SHA}&per_page=50" \
                | jq -c --arg apply "$apply_lc" '[.workflow_runs[] | select((.name // "") | ascii_downcase | contains($apply))] | first // {}')"
    status="$(jq -r '.status // ""' <<<"$run_json")"
    conclusion="$(jq -r '.conclusion // ""' <<<"$run_json")"
    [[ "$status" == "completed" ]] && break
    sleep 20
  done
  echo "::endgroup::"

  if [[ "$status" != "completed" ]]; then
    echo "apply-result=timeout" >> "$GITHUB_OUTPUT"
    echo "::error::terragrunt apply for ${MERGE_SHA} did not complete within ${APPLY_TIMEOUT}s (status='${status:-none}')."
    exit 1
  fi
  if [[ "$conclusion" != "success" ]]; then
    echo "apply-result=failure" >> "$GITHUB_OUTPUT"
    echo "::error::terragrunt apply concluded '${conclusion}' (expected success)."
    exit 1
  fi
  echo "apply-result=success" >> "$GITHUB_OUTPUT"
  echo "::notice::terragrunt apply succeeded for ${MERGE_SHA}."
}

revert() {
  REVERT_BRANCH="auto/ecr-revert-${MERGE_SHA:0:12}"
  echo "::group::Revert ${MERGE_SHA} after failed terragrunt apply"
  git config user.name "ecr-request-bot"
  git config user.email "ecr-request-bot@users.noreply.github.com"

  # Branch off base branch
  git fetch origin "$BASE_BRANCH"
  git checkout -B "$REVERT_BRANCH" "origin/${BASE_BRANCH}"

  # Run git revert and push
  if ! git revert --no-edit "$MERGE_SHA"; then
    git revert --abort || true
    echo "::error::Could not cleanly revert ${MERGE_SHA}; manually restore ${REPOS_LIST}."
    exit 1
  fi
  git push --force --set-upstream origin "$REVERT_BRANCH"

  PR_URL=$(gh pr create --repo "$TERRAFORM_REPO" --base "$BASE_BRANCH" --head "$REVERT_BRANCH" \
    --title "revert: remove ECR repos after failed terragrunt apply" \
    --body "Automated rollback of ${MERGE_SHA}: terragrunt apply failed.

Restores \`${REPOS_LIST}\` to its pre-request state. Triggered by **${SOURCE_REPO}**.")

  gh pr merge "$PR_URL" --repo "$TERRAFORM_REPO" --squash --admin --delete-branch
  echo "::notice::Reverted ${MERGE_SHA} via ${PR_URL}."
  echo "::endgroup::"
}

case "${1:-}" in
  setup-yq)    setup_yq ;;
  request)     request ;;
  verify-plan) verify_plan ;;
  merge)       merge_pr ;;
  wait-apply)  wait_apply ;;
  revert)      revert ;;
  *) echo "::error::Unknown subcommand '${1:-}'. Use setup-yq|request|verify-plan|merge|wait-apply|revert."; exit 1 ;;
esac
