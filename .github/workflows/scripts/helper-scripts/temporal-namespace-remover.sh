#!/usr/bin/env bash
# Deletes Temporal namespaces created for ephemeral PR releases by
# temporal-namespace-creator.sh, via the admintools pod of the in-cluster
# Temporal deployment.
#
#   usage: temporal-namespace-remover.sh --pr-number <n>
#          temporal-namespace-remover.sh <namespace> [namespace ...]
#
# --pr-number asks the Temporal server what actually exists and deletes every
# namespace matching -pr-<n> (anchored, so PR 12 never matches PR 123) — used
# by the PR-closed cleanup, where asking beats guessing a name per service and
# still covers a namespace whose Helm release already disappeared. The
# explicit-names form deletes exactly what it is given — used by the nightly
# sweep, which already knows which releases it removed.
#
# Deleting a namespace terminates any workflows still running in it, which for
# a dead PR environment is the point: retention only ever purges closed
# workflow histories, never running workflows. Every failure here degrades to
# a ::warning:: rather than a hard exit — a leaked namespace is inert once its
# workers are gone, and the caller's remaining cleanup (GitHub environments,
# deployments) must still run; the next cleanup run is the retry.
set -euo pipefail

admintools_exec() {
  kubectl exec -n temporal deployment/temporal-admintools -c admin-tools -- temporal "$@"
}

delete_namespace() {
  local ns="$1" out
  if out=$(admintools_exec operator namespace delete "${ns}" --yes 2>&1); then
    echo "Deleted Temporal namespace ${ns}"
  else
    echo "::warning::Failed to delete Temporal namespace '${ns}': ${out}"
  fi
}

if [[ "${1:-}" == "--pr-number" ]]; then
  pr_number="${2:?usage: $0 --pr-number <n>}"
  if [[ ! "${pr_number}" =~ ^[0-9]+$ ]]; then
    echo "::error::PR number '${pr_number}' is not numeric." >&2
    exit 1
  fi

  if ! raw=$(admintools_exec operator namespace list -o json 2>&1); then
    echo "::warning::Could not list Temporal namespaces: ${raw}"
    exit 0
  fi
  if ! names=$(jq -r '.[].namespaceInfo.name' <<< "${raw}" 2>&1); then
    echo "::warning::Could not parse Temporal namespace list: ${names}"
    exit 0
  fi

  namespaces=$(grep -E -- "-pr-${pr_number}\$" <<< "${names}" || true)
  if [[ -z "${namespaces}" ]]; then
    echo "No Temporal namespaces found for PR-${pr_number}."
    exit 0
  fi

  while IFS= read -r ns; do
    [[ -z "${ns}" ]] && continue
    delete_namespace "${ns}"
  done <<< "${namespaces}"
else
  if [[ $# -eq 0 ]]; then
    echo "usage: $0 --pr-number <n> | <namespace> [namespace ...]" >&2
    exit 1
  fi
  for ns in "$@"; do
    [[ -z "${ns}" ]] && continue
    delete_namespace "${ns}"
  done
fi
