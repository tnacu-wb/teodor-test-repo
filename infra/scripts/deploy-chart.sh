#!/usr/bin/env bash
#
# deploy-chart.sh — deploy a single Helm chart for the Helm Deploy workflow.
#
# Consumed by .github/workflows/helm-deploy.yml. All inputs arrive as environment
# variables (never interpolated into a shell body by the workflow), so untrusted
# values cannot inject shell.
#
# Inputs (env):
#   CHART_PATH       - chart path relative to INFRA_DIR (e.g. "temporal").
#                      Trusted: produced by detect-charts.sh (validated).
#   ENVIRONMENT      - target environment (dev|dit|sit|uat|perf|tooling|hulk|wanda).
#   INPUT_NAMESPACE  - optional namespace override (user-controlled; validated).
#   INFRA_DIR        - path to infra/ (default: "infra").
#   HELM_TIMEOUT     - helm --timeout (default: "15m").
#   EXTRA_SET        - optional single "key=value" passed as an extra --set (e.g.
#                      "image.tag=2.0.0-abc1234"). Empty by default; validated to
#                      a safe key=value shape. Lets a caller pin a built image
#                      tag without committing it to values.
#
# Behaviour summary:
#   * Release name = basename of CHART_PATH; namespace = INPUT_NAMESPACE or release.
#   * --atomic ON for stable envs (sit/uat/perf/hulk/wanda), OFF for dev/dit.
#   * A pre-existing failed/pending release is recovered by rolling back to the
#     last DEPLOYED revision when one exists (preserving workloads), and only
#     uninstalled when history has NO deployed revision (dead first install).
#     This holds for atomic and non-atomic envs alike.
#   * On a non-atomic upgrade failure, roll back immediately to the pre-upgrade
#     revision (helm won't do it without --atomic); rollback errors are surfaced.
#
set -euo pipefail

INFRA_DIR="${INFRA_DIR:-infra}"
HELM_TIMEOUT="${HELM_TIMEOUT:-15m}"

log()  { echo "$@" >&2; }
die()  { echo "::error::$*" >&2; exit 1; }

# Whole-string validators (bash regex, not line-oriented grep — so an embedded
# newline cannot slip a second line past the check).
is_dns1123_label() {
  [[ "$1" =~ ^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?$ ]]
}
is_safe_chart_path() {
  [[ "$1" =~ ^[A-Za-z0-9._/-]+$ ]] && [[ "$1" != *..* ]] && [[ "$1" != /* ]]
}
# A single helm --set argument: dotted/bracketed key = value, both restricted to
# a safe character set so a caller-supplied override can't inject extra flags or
# shell. Deliberately narrow (image tags, simple scalars); no spaces or commas.
# Bracket-expression care: ']' must come first, '-' must come last.
is_safe_set_arg() {
  [[ "$1" =~ ^[A-Za-z0-9_.][]A-Za-z0-9_.[-]*=[A-Za-z0-9_.:/-]+$ ]]
}

# True only for Helm 3's specific "release: not found" diagnostic — the one
# error that safely means "this release is absent". Deliberately narrow so
# unrelated errors (namespace/API/RBAC/404) are NOT treated as absent.
is_release_not_found() {
  printf '%s' "$1" | grep -qiE 'release:[[:space:]]*not found'
}

# Read a release's current Helm status. Distinguishes three outcomes so callers
# never confuse "cluster unreachable" with "release absent":
#   * prints the status string and returns 0 when the release exists;
#   * prints "not-found" and returns 0 when Helm confirms no such release;
#   * returns non-zero (prints nothing) when the status could not be read
#     (API/auth error) — callers MUST treat this as "unknown", not "absent".
release_status() {
  local release="$1" ns="$2" out rc
  out="$(helm status "$release" -n "$ns" -o json 2>&1)"; rc=$?
  if [ "$rc" -eq 0 ]; then
    printf '%s' "$out" | jq -r '.info.status' 2>/dev/null && return 0
    return 1  # got output but couldn't parse it — treat as unknown
  fi
  # Non-zero: only Helm's specific "release: not found" diagnostic is safe to
  # treat as absent. A broad "not found" match would misclassify unrelated
  # errors (e.g. 'namespaces "x" not found', 404s, RBAC failures) as an absent
  # release and skip the fail-closed abort.
  if is_release_not_found "$out"; then
    echo "not-found"
    return 0
  fi
  return 1  # real error (unreachable/auth/etc.) — unknown, do NOT assume absent
}

# Last successfully deployed revision number. Prints the revision (or empty when
# the release genuinely has no deployed revision) and returns 0; returns non-zero
# WITHOUT printing when history cannot be read/parsed, so callers can refuse to
# make a destructive decision on unreadable history.
last_deployed_revision() {
  local release="$1" ns="$2" hist rc rev
  # --max 0 = full history. Helm defaults to the most recent 256 revisions; on a
  # long-lived release whose last DEPLOYED revision is older than that window,
  # the default would return empty and the recovery path would then wrongly
  # treat a live release as a dead first install and uninstall it. Ask for the
  # complete history so that destructive decision is never made blind.
  hist="$(helm history "$release" -n "$ns" --max 0 -o json 2>&1)"; rc=$?
  if [ "$rc" -ne 0 ]; then
    # Helm's specific "release: not found" is a legitimate empty history; any
    # other error (API/storage/RBAC) must abort, not be papered over as empty.
    if is_release_not_found "$hist"; then
      echo ""
      return 0
    fi
    return 1
  fi
  # A known-good baseline is the highest-revision entry whose status is
  # "deployed" OR "superseded". Helm marks a previously successful revision
  # "superseded" once a newer revision is created, so after a failed non-atomic
  # upgrade history is typically [N: superseded, N+1: failed]; filtering on
  # "deployed" alone would miss revision N and wrongly treat a live release as a
  # dead first install. (Helm itself uses deployed|superseded as the rollback
  # baseline — pkg/action/upgrade.go.)
  rev="$(printf '%s' "$hist" \
    | jq -r '[.[] | select(.status == "deployed" or .status == "superseded")] | max_by(.revision) | .revision // empty' 2>/dev/null)" \
    || return 1
  printf '%s' "$rev"
  return 0
}

# Guard against destructive data loss before a `helm uninstall`. helm uninstall
# deletes the resources it rendered — INCLUDING PersistentVolumeClaims — and
# ebs-sc-retain only retains the underlying PV, not the claim. Crucially, Helm's
# "resource-policy: keep" is honored ONLY when it is baked into the last
# deployed release's rendered manifest; annotating a live PVC right before
# uninstall does NOT make Helm skip it (helm/helm#8132). So there is no reliable
# way to uninstall-but-keep a PVC from outside the chart. If the release owns any
# PVCs, we therefore FAIL CLOSED: refuse the automatic uninstall and require a
# human to decide, rather than silently deleting a database claim (dit/sit/uat
# Bitnami PostgreSQL). Charts with no PVCs (e.g. dev on Aurora) uninstall freely.
assert_no_release_pvcs() {
  local release="$1" ns="$2" all pvcs
  # Ownership can't be proven from a single label: the well-known
  # app.kubernetes.io/instance label is optional and not all charts set it on
  # their PVCs. So list ALL PVCs in the namespace and match any that carry
  # EITHER that instance label OR Helm's own meta.helm.sh/release-name
  # annotation equal to this release. Listing all PVCs (rather than a label
  # selector) avoids missing an unlabelled-but-owned claim. Fail closed if the
  # listing itself errors.
  if ! all="$(kubectl get pvc -n "$ns" -o json 2>&1)"; then
    die "Could not list PVCs for '$ns' (${all}) — aborting rather than risk deleting a data claim on uninstall."
  fi
  pvcs="$(printf '%s' "$all" | jq -r --arg r "$release" '
    .items[]
    | select(
        (.metadata.labels["app.kubernetes.io/instance"] == $r)
        or (.metadata.annotations["meta.helm.sh/release-name"] == $r)
      )
    | .metadata.name' 2>/dev/null)" \
    || die "Could not parse PVC list for '$ns' — aborting rather than risk deleting a data claim on uninstall."
  if [ -n "$pvcs" ]; then
    die "Refusing to auto-uninstall failed release '$release' in '$ns': it owns PVC(s) that helm uninstall would delete (helm cannot preserve them from outside the chart). Resolve manually — e.g. verify the data, then 'helm uninstall $release -n $ns' after backing up / retaining the claim. PVC(s): $(printf '%s' "$pvcs" | tr '\n' ' ')"
  fi
}

# Uninstall a release and CONFIRM it is gone. Unlike a bare "uninstall || true",
# this fails loudly if the release lingers in Helm storage (timeout, finalizer,
# API error), because a lingering release breaks the subsequent upgrade --install.
# Refuses to run if the release owns PVCs (see assert_no_release_pvcs).
uninstall_and_confirm() {
  local release="$1" ns="$2" status
  assert_no_release_pvcs "$release" "$ns"
  if ! helm uninstall "$release" -n "$ns" --wait; then
    die "helm uninstall of '$release' in '$ns' failed — refusing to continue with a release still in Helm storage."
  fi
  status="$(release_status "$release" "$ns")" \
    || die "Could not confirm '$release' was removed from '$ns' (status unreadable) — aborting."
  [ "$status" = "not-found" ] \
    || die "Release '$release' still present in '$ns' after uninstall (status: $status) — aborting."
}

# Wait for an in-progress uninstall to finish (release disappears), or fail.
wait_for_uninstall() {
  local release="$1" ns="$2" status
  for _ in $(seq 1 30); do
    status="$(release_status "$release" "$ns")" || die "Status of '$release' in '$ns' unreadable while waiting for uninstall — aborting."
    [ "$status" = "not-found" ] && return 0
    [ "$status" = "uninstalling" ] || return 0  # moved to some other state; let caller handle
    sleep 10
  done
  die "Release '$release' in '$ns' still uninstalling after timeout — aborting."
}

# Recover a release stuck failed/pending/uninstalling before we attempt the
# upgrade. Core safety rule: we only ever UNINSTALL a release we can positively
# confirm has no deployed revision. If Helm status or history can't be read, we
# ABORT rather than risk tearing down live workloads on a transient API error.
recover_failed_or_pending() {
  local release="$1" ns="$2" status="$3" last

  # Read history; a non-zero return means "unreadable", NOT "empty". Guard the
  # call with `if` so `set -e` doesn't abort before we can emit a clear error.
  if ! last="$(last_deployed_revision "$release" "$ns")"; then
    die "Cannot read Helm history for '$release' in '$ns' (status: $status) — refusing to recover blind, as an unreadable history could otherwise cause a live release to be uninstalled. Aborting."
  fi

  if [ -n "$last" ]; then
    # A good revision exists (even for a 'failed' release — e.g. an atomic
    # auto-rollback that itself failed). Roll back to it; never uninstall, so we
    # never wipe live workloads (SRE-352).
    log "⚠️  Release '$release' in '$status' has deployed revision $last — rolling back to it (preserving workloads)"
    # Use the same timeout as the deploy: helm rollback's default is 5m, which
    # can be shorter than a revision needs to become ready and fail spuriously.
    helm rollback "$release" "$last" -n "$ns" --wait --timeout "$HELM_TIMEOUT"
  else
    # Confirmed: no deployed revision ever existed (dead first install). helm
    # cannot upgrade a release stuck in this state ('no deployed releases'), so
    # uninstall it — and CONFIRM it's gone — to let the retry proceed.
    # uninstall_and_confirm refuses if the release owns PVCs (helm can't preserve
    # them from outside the chart), so a stateful chart requires manual handling
    # rather than silent data loss.
    log "⚠️  Release '$release' in '$status' with no deployed revision (dead first install) — uninstalling so retry can proceed"
    uninstall_and_confirm "$release" "$ns"
  fi
}

prepare_release() {
  local release="$1" ns="$2" status

  status="$(release_status "$release" "$ns")" \
    || die "Could not read Helm status for '$release' in '$ns' (cluster/API error) — aborting rather than risk a destructive recovery on unknown state."
  log "Current release status: $status"

  case "$status" in
    not-found|deployed|superseded)
      log "Release '$release' status '$status' needs no preparation."
      ;;
    pending-install|pending-upgrade|pending-rollback|failed)
      recover_failed_or_pending "$release" "$ns" "$status"
      ;;
    uninstalling)
      # An uninstall is already in flight; jumping to upgrade --install now can be
      # rejected while the release is still present/locked. Wait for it to clear.
      # If it resolves to something other than not-found (e.g. the uninstall
      # aborted into 'failed'), re-run preparation so that new state is handled
      # by the proper branch instead of deploying over it blindly.
      log "Release '$release' is currently uninstalling — waiting for it to complete before deploying"
      wait_for_uninstall "$release" "$ns"
      local after
      after="$(release_status "$release" "$ns")" \
        || die "Could not read status of '$release' in '$ns' after uninstall wait — aborting."
      if [ "$after" != "not-found" ]; then
        log "Release '$release' settled into '$after' after uninstall — re-running preparation"
        prepare_release "$release" "$ns"
      fi
      ;;
    *)
      # Unknown/unexpected status — don't guess. Fail so a human can look.
      die "Release '$release' in '$ns' is in unexpected status '$status' — aborting rather than deploying over an unknown state."
      ;;
  esac
}

# Build the --values flag list, adding each file only when it exists relative to
# the chart dir (charts ship different value files; the shared base chart ships
# none under values/). Echoes the space-separated flag string.
values_args() {
  local chart_dir="$1" env="$2" args=""
  # Append the flag only when the file exists. Written as an explicit if (not
  # `[ -f ] && ... || return 0`) so it is unambiguous AND set -e-safe: a missing
  # file must NOT make add return non-zero, or a bare add call would abort
  # values_args under errexit (it currently only "works" via command
  # substitution suppressing errexit — a fragile accident we don't rely on).
  add() {
    if [ -f "${chart_dir}/$1" ]; then
      args="${args} --values $1"
    fi
    return 0
  }
  add "values/values.yaml"
  if [ "$env" = "hulk" ] || [ "$env" = "wanda" ]; then
    add "values/prod.yaml"
  else
    add "values/nonprod.yaml"
    add "values/${env}.yaml"
  fi
  echo "${args# }"
}

main() {
  local chart_path="${CHART_PATH:?CHART_PATH is required}"
  local env="${ENVIRONMENT:?ENVIRONMENT is required}"

  is_safe_chart_path "$chart_path" || die "Unsafe CHART_PATH '${chart_path}'."

  local chart_dir="${INFRA_DIR}/${chart_path}"
  [ -f "${chart_dir}/Chart.yaml" ] || die "No Chart.yaml at ${chart_dir}."

  local release namespace
  release="$(basename "$chart_path")"
  namespace="${INPUT_NAMESPACE:-}"
  [ -n "$namespace" ] || namespace="$release"
  is_dns1123_label "$namespace" \
    || die "Invalid namespace '${namespace}': must be a single DNS-1123 label."

  # Atomic decision: OFF for the churny lower envs, ON elsewhere.
  local use_atomic
  case "$env" in
    dev|dit) use_atomic="false" ;;
    *)       use_atomic="true"  ;;
  esac

  local vargs
  vargs="$(values_args "$chart_dir" "$env")"
  log "Release=$release Namespace=$namespace Env=$env Atomic=$use_atomic"
  log "Values args: ${vargs:-<none>}"

  prepare_release "$release" "$namespace"

  # Record the pre-upgrade good revision so a non-atomic failure can be rolled
  # back immediately (helm won't auto-roll-back without --atomic, and
  # --cleanup-on-fail does not restore pre-existing resources). Capture the
  # function's exit code separately (command substitution would otherwise mask
  # it) and abort if history is unreadable — we must not proceed into a
  # non-atomic upgrade unable to recover it.
  local pre_deployed=""
  if [ "$use_atomic" != "true" ]; then
    if ! pre_deployed="$(last_deployed_revision "$release" "$namespace")"; then
      die "Cannot read Helm history for '$release' in '$namespace' before a non-atomic deploy — aborting (a failed upgrade could not be safely rolled back)."
    fi
  fi

  local atomic_flag=()
  [ "$use_atomic" = "true" ] && atomic_flag=(--atomic)

  # Optional single --set override (e.g. a freshly built image tag). Validated
  # to a safe key=value so it can't inject extra helm flags. Empty by default.
  local set_flag=()
  if [ -n "${EXTRA_SET:-}" ]; then
    is_safe_set_arg "$EXTRA_SET" \
      || die "Invalid EXTRA_SET '${EXTRA_SET}': must be a single safe key=value (e.g. image.tag=2.0.0-abc1234)."
    set_flag=(--set "$EXTRA_SET")
    log "Extra override: --set $EXTRA_SET"
  fi
  # NOTE: a plain "${arr[@]}" on an EMPTY array trips `set -u` on bash < 4.4
  # (shipped on macOS and some runners). The ${arr[@]+"${arr[@]}"} form below
  # expands to nothing when empty and to the quoted element(s) otherwise — the
  # set -u-safe idiom (and avoids passing a stray empty-string argument).

  # vargs is a controlled flag string from values_args(); intentional splitting.
  local rc=0
  # shellcheck disable=SC2086  # $vargs must word-split into separate helm flags
  ( cd "$chart_dir" && \
    helm upgrade "$release" . \
      --install \
      --namespace "$namespace" \
      $vargs \
      ${set_flag[@]+"${set_flag[@]}"} \
      --wait \
      --timeout "$HELM_TIMEOUT" \
      ${atomic_flag[@]+"${atomic_flag[@]}"} \
      --cleanup-on-fail ) || rc=$?

  if [ "$rc" -ne 0 ]; then
    if [ "$use_atomic" != "true" ] && [ -n "$pre_deployed" ]; then
      log "❌ Upgrade failed — rolling back to last deployed revision $pre_deployed to restore the working state"
      # Surface a rollback failure: if recovery itself fails, dev/dit are NOT
      # protected and the operator must know.
      if ! helm rollback "$release" "$pre_deployed" -n "$namespace" --wait --timeout "$HELM_TIMEOUT"; then
        die "helm upgrade failed (rc=$rc) AND rollback to revision $pre_deployed failed — release '$release' in '$namespace' may be partially upgraded."
      fi
    fi
    die "helm upgrade for '$release' in '$namespace' failed (rc=$rc)."
  fi

  log "✅ helm upgrade for $release in $namespace reported success"

  # Verify the release really is healthy. helm upgrade --wait already blocked on
  # readiness, but a post-upgrade API/permission failure must NOT be swallowed
  # and reported as a healthy deploy — so confirm the status is 'deployed' and
  # let a read failure fail the job.
  local final_status
  final_status="$(release_status "$release" "$namespace")" \
    || die "Deploy of '$release' in '$namespace' could not be verified (status read failed after upgrade)."
  [ "$final_status" = "deployed" ] \
    || die "Deploy of '$release' in '$namespace' finished in status '$final_status', expected 'deployed'."
  log "📊 Verified release status: $final_status"
  # Pods listing is informational; don't fail the job on a transient list error.
  log "📊 Pods:"; kubectl get pods -n "$namespace" >&2 || true

  log "✅ Successfully deployed $release to $namespace"

  # Post-deploy hook. Contract is NAMESPACE + CHART, where CHART is the release
  # name (hooks build e.g. "${CHART}-admintools"). Also expose CHART_PATH.
  local hook="${chart_dir}/scripts/post-deploy.sh"
  if [ -f "$hook" ]; then
    log "Running post-deploy hook: $hook"
    chmod +x "$hook"
    NAMESPACE="$namespace" CHART="$release" CHART_PATH="$chart_path" "$hook"
  else
    log "No post-deploy hook at $hook — skipping"
  fi
}

# Run main only when executed directly, so tests can source this file and call
# individual functions (e.g. values_args) in isolation.
if [ "${BASH_SOURCE[0]}" = "${0}" ]; then
  main "$@"
fi
