#!/usr/bin/env bash
set -euo pipefail

: "${BRANCH_NAME:?BRANCH_NAME is required}"
: "${GITHUB_OUTPUT:?GITHUB_OUTPUT is required}"

app_name=${APP_NAME:-}
pr_number=${PR_NUMBER:-0}
if [[ -z "$pr_number" || "$pr_number" == "null" ]]; then
  pr_number=0
fi

# Which values file to point the deploy at. `ephemeral` is the per-PR preview
# and the only caller today; `dev` is the long-lived release, for when the dev
# deploy in ci-fe-deploy.yaml is re-enabled.
values_variant=${VALUES_VARIANT:-ephemeral}
case "$values_variant" in
  ephemeral | dev) ;;
  *)
    printf 'Unsupported values variant: %s (expected ephemeral or dev)\n' "$values_variant" >&2
    exit 1
    ;;
esac

sanitized_branch=$(printf '%s' "$BRANCH_NAME" \
  | tr '[:upper:]' '[:lower:]' \
  | sed -E 's#[^a-z0-9/-]#-#g; s#/#-#g; s#-+#-#g; s#^-+##; s#-+$##')
sanitized_branch=${sanitized_branch:0:35}
sanitized_branch=$(printf '%s' "$sanitized_branch" | sed -E 's#^-+##; s#-+$##')
sanitized_branch=${sanitized_branch:-pr}

issue_number=""
if [[ "$BRANCH_NAME" == *-Issues-XX ]]; then
  issue_number=$(printf '%s' "$BRANCH_NAME" | sed -nE 's#.*CTECH-([0-9]+)-Issues-XX$#\1#p')
fi

jira_key=""
if [[ "$BRANCH_NAME" == *-JIRA-XX ]]; then
  jira_key=$(printf '%s' "$BRANCH_NAME" \
    | sed -nE 's#.*(CTECH-[0-9]+)-JIRA-XX$#\1#p' \
    | tr '[:lower:]' '[:upper:]')
fi

release_name=""
host_name=""
values_path=""
image_repo=""
environment_name=""
# The frontend deploys with the same shared chart as the backend services. The
# per-app chart that used to live under frontend/pi-front-end-applications/helm
# is gone; each app keeps only its values files, next to the app.
chart_path="infra/helm/opera-app-chart"

if [[ -n "$app_name" ]]; then
  case "$app_name" in
    business-booker)
      host_suffix="business.dev.premierinn.digital"
      ;;
    ccui)
      host_suffix="ccui.dev.premierinn.digital"
      ;;
    premier-inn)
      host_suffix="dev.premierinn.digital"
      ;;
    *)
      printf 'Unsupported frontend app: %s\n' "$app_name" >&2
      exit 1
      ;;
  esac

  # Ephemeral previews key the release (and therefore host) on the PR number so
  # both trigger paths — the POC pull_request gate and the "Ephemeral UP" comment
  # — converge on the same <app>-PR-<n> release, and cleanup can find it by PR
  # number. The long-lived dev release (values_variant=dev) and any no-PR context
  # (pr_number=0) stay branch-based.
  if [[ "$values_variant" == "ephemeral" && "$pr_number" != "0" ]]; then
    release_name="${app_name}-pr-${pr_number}"
  else
    release_name="${app_name}-${sanitized_branch}"
  fi
  host_name="${release_name}.${host_suffix}"
  values_path="frontend/pi-front-end-applications/apps/next-apps/${app_name}/helm/${values_variant}-values.yaml"
  image_repo="whitbreaddigital/${app_name}"
  environment_name="${app_name}-PR-${pr_number}"
fi

{
  printf 'app-name=%s\n' "$app_name"
  printf 'pr-number=%s\n' "$pr_number"
  printf 'release-name=%s\n' "$release_name"
  printf 'host-name=%s\n' "$host_name"
  printf 'chart-path=%s\n' "$chart_path"
  printf 'values-path=%s\n' "$values_path"
  printf 'values-variant=%s\n' "$values_variant"
  printf 'image-repo=%s\n' "$image_repo"
  printf 'environment-name=%s\n' "$environment_name"
  printf 'sanitized-branch=%s\n' "$sanitized_branch"
  printf 'issue-number=%s\n' "$issue_number"
  printf 'jira-key=%s\n' "$jira_key"
} >> "$GITHUB_OUTPUT"
