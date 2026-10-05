#!/usr/bin/env bash
# Creates a Temporal namespace for an ephemeral PR release, via the admintools
# pod of the in-cluster Temporal deployment.
#
#   usage: temporal-namespace-creator.sh <namespace> [values-file ...]
#
# Temporal namespaces are not auto-created, and a worker pointed at a missing
# one fails to start — so the namespace has to exist before the pods the helm
# upgrade creates come up. When values files are passed, creation is gated on
# one of them mentioning TEMPORAL_NAMESPACE, so services that never configure
# Temporal don't each mint an unused namespace; empty file arguments are
# ignored, letting callers pass optional paths verbatim. Retention (72h) only
# bounds how long closed workflow histories are kept; the namespace itself
# lives until temporal-namespace-remover.sh deletes it, and an orphaned one
# with no workers is inert.
#
# `create` on an existing namespace is the expected redeploy case, so exactly
# that error is tolerated; anything else fails, because the alternative is the
# app pod crash-looping against a missing namespace with a far less obvious
# error.
set -euo pipefail

namespace="${1:?usage: $0 <namespace> [values-file ...]}"
shift

values_files=()
for f in "$@"; do
  [[ -n "$f" ]] && values_files+=("$f")
done

if (( ${#values_files[@]} > 0 )) && ! grep -qs "TEMPORAL_NAMESPACE" "${values_files[@]}"; then
  echo "TEMPORAL_NAMESPACE not configured in: ${values_files[*]} — skipping namespace creation."
  exit 0
fi

if out=$(kubectl exec -n temporal deployment/temporal-admintools -c admin-tools -- \
    temporal operator namespace create "${namespace}" --retention 72h 2>&1); then
  echo "Created Temporal namespace ${namespace}"
elif echo "${out}" | grep -qi "already exists"; then
  echo "Temporal namespace ${namespace} already exists; nothing to do."
else
  echo "${out}" >&2
  exit 1
fi
