#!/usr/bin/env bash
#
# Sweep ephemeral (per-pull-request) Helm releases out of the dev cluster.
#
# Ephemeral releases are recognised by name — see README.md in this directory:
#   canonical        <service>-pr-<number>     ci-matrix-deploy-eph.yaml  (opera-be)
#   frontend legacy  <app>-<sanitized-branch>  ci-deploy-fe.yaml          (opera-fe)
#
# The bare name of any app or service in the repo is protected: both patterns require a
# suffix, so a permanent release can never be selected.
#
# Environment:
#   NAMESPACES      Comma/space separated namespaces to sweep. Required.
#   DRY_RUN         true|yes|false|no — list the plan and uninstall nothing. Default true.
#   SKIP_OPEN_PRS   true|yes|false|no — keep releases belonging to a still-open PR.
#                   Default false.
#   RELEASE_FILTER  Optional extended regex; only matching releases are considered.
#   OPEN_PRS        JSON [{"number":1,"branch":"feat/x"}, ...]; required when
#                   SKIP_OPEN_PRS=true, supplied by the calling workflow.
#
# Exits non-zero if any uninstall failed. A release that is merely stuck does not stop the
# rest of the sweep.

set -euo pipefail

# Normalises the flag spellings a caller may reach for — the workflow dropdown, the REST API
# and `gh workflow run --field` all hand these through as free-text strings. Anything not
# recognised is an error rather than a silent falsy, so a typo can never uninstall by accident.
as_bool() {
  local name="$1" value
  value=$(printf '%s' "$2" | tr '[:upper:]' '[:lower:]')
  case "$value" in
    true | yes | y | 1 | on) echo true ;;
    false | no | n | 0 | off) echo false ;;
    *)
      echo "::error::${name} must be true/false (yes/no accepted); got '${2}'." >&2
      return 1
      ;;
  esac
}

DRY_RUN=$(as_bool DRY_RUN "${DRY_RUN:-true}")
SKIP_OPEN_PRS=$(as_bool SKIP_OPEN_PRS "${SKIP_OPEN_PRS:-false}")
RELEASE_FILTER="${RELEASE_FILTER:-}"
OPEN_PRS="${OPEN_PRS:-[]}"

if [[ -z "${NAMESPACES:-}" ]]; then
  echo "::error::NAMESPACES is required."
  exit 1
fi

read -r -a namespaces <<< "${NAMESPACES//,/ }"

# Mirrors the branch sanitisation in ci-deploy-fe.yaml / ci-matrix-deploy-eph.yaml exactly.
# Any drift here silently un-protects open frontend PRs, so keep the three in step.
sanitize_branch() {
  local branch
  branch=$(echo "$1" | tr '[:upper:]' '[:lower:]' | sed -E 's#[^a-z0-9/-]#-#g; s#/#-#g; s#-+#-#g; s#^-+##; s#-+$##')
  branch=${branch:0:35}
  branch=$(echo "$branch" | sed -E 's#^-+##; s#-+$##')
  echo "${branch:-pr}"
}

# --- What the repo owns -------------------------------------------------------------

mapfile -t fe_apps < <(
  find frontend/pi-front-end-applications/apps/next-apps -mindepth 1 -maxdepth 1 -type d \
    -exec basename {} \; 2>/dev/null | sort -u
)
mapfile -t be_services < <(
  find backend/*/services -maxdepth 2 -name pom.xml -exec dirname {} \; 2>/dev/null \
    | xargs -r -I{} basename {} | sort -u
)

declare -A protected=()
for name in "${fe_apps[@]}" "${be_services[@]}"; do
  [[ -n "$name" ]] && protected["$name"]=1
done

fe_pattern=""
if ((${#fe_apps[@]})); then
  fe_pattern="^($(IFS='|'; echo "${fe_apps[*]}"))-.+$"
fi

echo "Frontend apps:    ${fe_apps[*]:-none}"
echo "Backend services: ${#be_services[@]} found"
echo "Namespaces:       ${namespaces[*]}"
echo "Dry run:          ${DRY_RUN}"
echo "Skip open PRs:    ${SKIP_OPEN_PRS}"
[[ -n "$RELEASE_FILTER" ]] && echo "Release filter:   ${RELEASE_FILTER}"

# --- Releases that belong to a still-open PR ----------------------------------------

declare -A live_names=()
live_suffixes=()

if [[ "$SKIP_OPEN_PRS" == "true" ]]; then
  while IFS=$'\t' read -r pr_number branch; do
    [[ -z "$pr_number" ]] && continue
    live_suffixes+=("-pr-${pr_number}")
    slug=$(sanitize_branch "$branch")
    for app in "${fe_apps[@]}"; do
      live_names["${app}-${slug}"]=1
    done
  done < <(printf '%s' "$OPEN_PRS" | jq -r '.[] | [.number, .branch] | @tsv')
  echo "Open PRs protected: ${#live_suffixes[@]}"
fi

is_live() {
  local release="$1" suffix
  [[ -n "${live_names[$release]:-}" ]] && return 0
  for suffix in "${live_suffixes[@]:-}"; do
    # Suffix match is anchored by construction: '-pr-12' cannot match '…-pr-123'.
    [[ -n "$suffix" && "$release" == *"$suffix" ]] && return 0
  done
  return 1
}

# --- Sweep ---------------------------------------------------------------------------

removed=()
failed=()
skipped=()
list_failed=()
orphan_deploys=()

for namespace in "${namespaces[@]}"; do
  [[ -z "$namespace" ]] && continue
  echo "::group::Namespace ${namespace}"

  # --all so failed / pending-install releases are swept too, not just healthy ones.
  if ! releases=$(helm list --namespace "$namespace" --all --short 2>/tmp/helm-list.err); then
    echo "::error::helm list failed for namespace '${namespace}':"
    cat /tmp/helm-list.err >&2
    list_failed+=("$namespace")
    echo "::endgroup::"
    continue
  fi

  if command -v kubectl >/dev/null 2>&1; then
    if deploys=$(kubectl get deploy --namespace "$namespace" -o name 2>/tmp/kubectl.err); then
      while IFS= read -r dep; do
        dep="${dep##*/}"
        [[ -z "$dep" ]] && continue
        if [[ "$dep" =~ -pr-[0-9]+$ ]] || { [[ -n "$fe_pattern" ]] && [[ "$dep" =~ $fe_pattern ]]; }; then
          # Ephemeral Deployment with no matching Helm release = orphan.
          grep -qxF "$dep" <<< "$releases" || orphan_deploys+=("${namespace}/${dep}")
        fi
      done <<< "$deploys"
    else
      echo "::warning::kubectl get deploy failed for '${namespace}'; orphan diagnostic skipped:"
      cat /tmp/kubectl.err >&2
    fi
  else
    echo "kubectl not found; skipping orphaned Deployment diagnostic."
  fi

  if [[ -z "$releases" ]]; then
    echo "No Helm releases in ${namespace}."
    echo "::endgroup::"
    continue
  fi

  while IFS= read -r release; do
    [[ -z "$release" ]] && continue

    if [[ -n "${protected[$release]:-}" ]]; then
      echo "keep      ${release} (permanent release)"
      continue
    fi

    ephemeral=false
    if [[ "$release" =~ -pr-[0-9]+$ ]]; then
      ephemeral=true
    elif [[ -n "$fe_pattern" && "$release" =~ $fe_pattern ]]; then
      ephemeral=true
    fi

    if [[ "$ephemeral" != "true" ]]; then
      echo "keep      ${release} (does not match an ephemeral pattern)"
      continue
    fi

    if [[ -n "$RELEASE_FILTER" && ! "$release" =~ $RELEASE_FILTER ]]; then
      echo "keep      ${release} (excluded by release-filter)"
      continue
    fi

    if [[ "$SKIP_OPEN_PRS" == "true" ]] && is_live "$release"; then
      echo "skip      ${release} (pull request still open)"
      skipped+=("${namespace}/${release}")
      continue
    fi

    if [[ "$DRY_RUN" == "true" ]]; then
      echo "would remove ${release}"
      removed+=("${namespace}/${release}")
      continue
    fi

    echo "uninstall ${release}"
    if helm uninstall "$release" --namespace "$namespace" --ignore-not-found --wait --timeout 5m; then
      removed+=("${namespace}/${release}")
    else
      # One stuck release must not abandon the rest of the sweep; it is reported at the end.
      echo "::warning::Failed to uninstall '${release}' from '${namespace}'; it may need removing by hand."
      failed+=("${namespace}/${release}")
    fi
  done <<< "$releases"

  echo "::endgroup::"
done

# --- Report --------------------------------------------------------------------------

summary="${GITHUB_STEP_SUMMARY:-/dev/stdout}"
verb=$([[ "$DRY_RUN" == "true" ]] && echo "Would uninstall" || echo "Uninstalled")

{
  echo "## Ephemeral environment cleanup"
  echo
  if [[ "$DRY_RUN" == "true" ]]; then
    echo "> **Dry run** — nothing was removed. Re-run with \`dry-run: false\` to apply."
    echo
  fi
  echo "Namespaces swept: \`${namespaces[*]}\`"
  echo

  echo "### ${verb} (${#removed[@]})"
  if ((${#removed[@]})); then
    printf -- '- `%s`\n' "${removed[@]}"
  else
    echo "_None._"
  fi

  if ((${#skipped[@]})); then
    echo
    echo "### Skipped — pull request still open (${#skipped[@]})"
    printf -- '- `%s`\n' "${skipped[@]}"
  fi

  if ((${#failed[@]})); then
    echo
    echo "### :warning: Failed — remove by hand (${#failed[@]})"
    printf -- '- `%s`\n' "${failed[@]}"
  fi

  if ((${#orphan_deploys[@]})); then
    echo
    echo "### :warning: Orphaned ephemeral Deployments — no Helm release (${#orphan_deploys[@]})"
    echo "A Helm-only sweep cannot remove these; they still reserve node capacity."
    printf -- '- `%s`\n' "${orphan_deploys[@]}"
  fi

  if ((${#list_failed[@]})); then
    echo
    echo "### :warning: Namespaces where \`helm list\` failed (${#list_failed[@]})"
    printf -- '- `%s`\n' "${list_failed[@]}"
  fi
} >> "$summary"

echo "${verb}: ${#removed[@]}, skipped: ${#skipped[@]}, failed: ${#failed[@]}, orphaned-deploys: ${#orphan_deploys[@]}, list-failures: ${#list_failed[@]}"

if ((${#orphan_deploys[@]})); then
  echo "::warning::${#orphan_deploys[@]} ephemeral Deployment(s) have no Helm release and were NOT removed (helm-only sweep): ${orphan_deploys[*]}"
fi

if ((${#list_failed[@]})); then
  echo "::error::helm list failed in ${#list_failed[@]} namespace(s): ${list_failed[*]}"
  exit 1
fi

if ((${#failed[@]})); then
  echo "::error::${#failed[@]} ephemeral release(s) could not be uninstalled: ${failed[*]}"
  exit 1
fi
