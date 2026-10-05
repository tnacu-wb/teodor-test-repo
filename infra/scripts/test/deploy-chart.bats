#!/usr/bin/env bats
#
# Tests for infra/scripts/deploy-chart.sh
#
# helm/kubectl are stubbed; no cluster is contacted. Stubs log ACTION_* markers
# to stderr so tests can assert which helm subcommands ran.

load test_helper

setup() {
  setup_workspace
  kubectl_stub
  DEPLOY="$SCRIPTS_DIR/deploy-chart.sh"
}

run_deploy() {
  # usage: run_deploy CHART_PATH ENV [INPUT_NAMESPACE]
  run env CHART_PATH="$1" ENVIRONMENT="$2" INPUT_NAMESPACE="${3:-}" "$DEPLOY"
}

# --- input validation ---------------------------------------------------------

@test "missing Chart.yaml is rejected" {
  mkdir -p "$INFRA_DIR/nochart"
  helm_stub_absent
  run_deploy "nochart" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"No Chart.yaml"* ]]
}

@test "invalid namespace override is rejected before any helm call" {
  make_chart "temporal"
  helm_stub_absent
  run_deploy "temporal" dev "Bad_NS"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid namespace"* ]]
}

@test "unsafe CHART_PATH is rejected" {
  helm_stub_absent
  run_deploy "../escape" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"Unsafe CHART_PATH"* ]]
}

# --- atomic decision & values selection ---------------------------------------

@test "dev uses no --atomic and layers dev values" {
  make_chart "temporal" --values values nonprod dev
  helm_stub_absent
  run_deploy "temporal" dev
  [ "$status" -eq 0 ]
  [[ "$output" == *"Atomic=false"* ]]
  [[ "$output" == *"--values values/values.yaml --values values/nonprod.yaml --values values/dev.yaml"* ]]
  [[ "$output" != *"--atomic"* ]]
}

@test "sit uses --atomic" {
  make_chart "temporal" --values values nonprod
  helm_stub_absent
  run_deploy "temporal" sit
  [ "$status" -eq 0 ]
  [[ "$output" == *"Atomic=true"* ]]
  [[ "$output" == *"ACTION_UPGRADE"* ]]
  [[ "$output" == *"--atomic"* ]]
}

@test "prod (hulk) uses prod values, not nonprod" {
  make_chart "temporal" --values values prod nonprod
  helm_stub_absent
  run_deploy "temporal" hulk
  [ "$status" -eq 0 ]
  [[ "$output" == *"--values values/values.yaml --values values/prod.yaml"* ]]
  [[ "$output" != *"nonprod"* ]]
}

@test "chart with no values/ dir passes no --values flags" {
  make_chart "bare"   # no --values
  helm_stub_absent
  run_deploy "bare" dev
  [ "$status" -eq 0 ]
  [[ "$output" != *"--values"* ]]
}

# Regression for the set -e / values_args issue: call values_args DIRECTLY under
# set -e (not via command substitution, which would mask an errexit abort). If
# 'add' aborts the function when a values file is missing, this fails.
@test "values_args does not abort under set -e when files are missing (direct call)" {
  make_chart "temporal" --values values nonprod   # no sit.yaml
  run bash -c '
    set -euo pipefail
    INFRA_DIR="'"$INFRA_DIR"'"
    # shellcheck disable=SC1090
    source "'"$SCRIPTS_DIR"'/deploy-chart.sh"
    out="$(values_args "'"$INFRA_DIR"'/temporal" sit)"
    values_args "'"$INFRA_DIR"'/temporal" sit >/dev/null   # bare statement under set -e
    echo "OK: $out"
  '
  [ "$status" -eq 0 ]
  [[ "$output" == *"OK: --values values/values.yaml --values values/nonprod.yaml"* ]]
}

@test "values_args does not abort under set -e for a bare chart (all files missing)" {
  make_chart "bare2"   # no values/ dir at all
  run bash -c '
    set -euo pipefail
    INFRA_DIR="'"$INFRA_DIR"'"
    # shellcheck disable=SC1090
    source "'"$SCRIPTS_DIR"'/deploy-chart.sh"
    values_args "'"$INFRA_DIR"'/bare2" dev >/dev/null   # bare statement under set -e
    echo "SURVIVED"
  '
  [ "$status" -eq 0 ]
  [[ "$output" == *"SURVIVED"* ]]
}

@test "namespace override is honoured when valid" {
  make_chart "temporal"
  helm_stub_absent
  run_deploy "temporal" dev "custom-ns"
  [ "$status" -eq 0 ]
  [[ "$output" == *"Namespace=custom-ns"* ]]
}

# --- happy path ---------------------------------------------------------------

@test "not-found release deploys without recovery" {
  make_chart "temporal"
  helm_stub_absent
  run_deploy "temporal" sit
  [ "$status" -eq 0 ]
  [[ "$output" == *"needs no preparation"* ]]
  [[ "$output" == *"Successfully deployed"* ]]
}

# --- recovery of failed/pending releases --------------------------------------

@test "failed release WITH deployed revision rolls back (never uninstalls)" {
  make_chart "temporal"
  helm_stub "failed" '[{"revision":2,"status":"deployed"},{"revision":3,"status":"failed"}]'
  run_deploy "temporal" sit
  [[ "$output" == *"rolling back to it"* ]]
  [[ "$output" == *"ACTION_ROLLBACK rollback temporal 2"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

@test "failed release with NO deployed revision uninstalls (confirmed gone)" {
  make_chart "temporal"
  # Lifecycle: 'failed' (no deployed rev) -> uninstall -> 'not-found' ->
  # upgrade -> 'deployed' (so post-deploy verification passes).
  write_stub helm '
UNINST="$BATS_TEST_TMPDIR/uninstalled"
DEPL="$BATS_TEST_TMPDIR/deployed"
case "$1" in
  status)
    if [ -f "$DEPL" ]; then echo "{\"info\":{\"status\":\"deployed\"}}";
    elif [ -f "$UNINST" ]; then echo "{\"info\":{\"status\":\"not-found\"}}";
    else echo "{\"info\":{\"status\":\"failed\"}}"; fi ;;
  history)   echo "[{\"revision\":1,\"status\":\"failed\"}]" ;;
  uninstall) echo "ACTION_UNINSTALL" >&2; : > "$UNINST" ;;
  upgrade)   echo "ACTION_UPGRADE" >&2; : > "$DEPL" ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [ "$status" -eq 0 ]
  [[ "$output" == *"dead first install"* ]]
  [[ "$output" == *"ACTION_UNINSTALL"* ]]
  [[ "$output" == *"ACTION_UPGRADE"* ]]
}

@test "failed release with 'superseded' prior revision rolls back (not uninstall)" {
  make_chart "temporal"
  # After a failed non-atomic upgrade helm marks the prior good revision
  # 'superseded': history = [N superseded, N+1 failed]. Recovery must pick N and
  # roll back, NOT treat it as a dead first install.
  helm_stub "failed" '[{"revision":4,"status":"superseded"},{"revision":5,"status":"failed"}]'
  run_deploy "temporal" sit
  [[ "$output" == *"rolling back to it"* ]]
  [[ "$output" == *"ACTION_ROLLBACK rollback temporal 4"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
  [[ "$output" != *"dead first install"* ]]
}

@test "dead-first-install refuses to uninstall when the release owns PVCs" {
  make_chart "temporal"
  kubectl_stub_with_pvc
  helm_stub "failed" '[{"revision":1,"status":"failed"}]'
  run_deploy "temporal" sit
  [ "$status" -ne 0 ]
  [[ "$output" == *"Refusing to auto-uninstall"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

@test "dead-first-install aborts if PVC listing fails (fail closed)" {
  make_chart "temporal"
  kubectl_stub_pvc_error
  helm_stub "failed" '[{"revision":1,"status":"failed"}]'
  run_deploy "temporal" sit
  [ "$status" -ne 0 ]
  [[ "$output" == *"Could not list PVCs"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

@test "failed release whose uninstall does not clear aborts (no blind deploy)" {
  make_chart "temporal"
  # status stays 'failed' even after uninstall -> uninstall_and_confirm must die
  write_stub helm '
case "$1" in
  status)  echo "{\"info\":{\"status\":\"failed\"}}" ;;
  history) echo "[{\"revision\":1,\"status\":\"failed\"}]" ;;
  uninstall) echo "ACTION_UNINSTALL" >&2 ;;
  upgrade) echo "ACTION_UPGRADE" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [ "$status" -ne 0 ]
  [[ "$output" == *"still present"* ]]
  [[ "$output" != *"ACTION_UPGRADE"* ]]
}

# --- fail-safe on unreadable cluster state ------------------------------------

@test "unreadable status aborts before any action" {
  make_chart "temporal"
  write_stub helm '
case "$1" in
  status) echo "Error: Kubernetes cluster unreachable" >&2; exit 1 ;;
  uninstall) echo "ACTION_UNINSTALL" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"Could not read Helm status"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

@test "failed release with unreadable history aborts (never uninstalls)" {
  make_chart "temporal"
  write_stub helm '
case "$1" in
  status)  echo "{\"info\":{\"status\":\"failed\"}}" ;;
  history) echo "Error: unreachable" >&2; exit 1 ;;
  uninstall) echo "ACTION_UNINSTALL" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"refusing to recover blind"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

# --- non-atomic upgrade failure -> immediate rollback -------------------------

@test "non-atomic upgrade failure rolls back to pre-upgrade revision" {
  make_chart "temporal"
  # status not-found initially so prepare is a no-op; history has a deployed rev
  # so pre_deployed is set; upgrade fails -> rollback to it.
  write_stub helm '
case "$1" in
  status)  echo "{\"info\":{\"status\":\"deployed\"}}" ;;
  history) echo "[{\"revision\":7,\"status\":\"deployed\"}]" ;;
  upgrade) echo "ACTION_UPGRADE" >&2; exit 1 ;;
  rollback) echo "ACTION_ROLLBACK $*" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"Upgrade failed"* ]]
  [[ "$output" == *"ACTION_ROLLBACK rollback temporal 7"* ]]
}

# --- uninstalling status ------------------------------------------------------

@test "uninstalling that clears then deploys" {
  make_chart "temporal"
  # Lifecycle: 'uninstalling' (1st status) -> 'not-found' (cleared) -> upgrade
  # -> 'deployed' (final verification passes).
  write_stub helm '
SEEN="$BATS_TEST_TMPDIR/seen"
DEPL="$BATS_TEST_TMPDIR/deployed"
case "$1" in
  status)
    if [ -f "$DEPL" ]; then echo "{\"info\":{\"status\":\"deployed\"}}";
    elif [ -f "$SEEN" ]; then echo "{\"info\":{\"status\":\"not-found\"}}";
    else : > "$SEEN"; echo "{\"info\":{\"status\":\"uninstalling\"}}"; fi ;;
  history) echo "[]" ;;
  upgrade) echo "ACTION_UPGRADE" >&2; : > "$DEPL" ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [ "$status" -eq 0 ]
  [[ "$output" == *"currently uninstalling"* ]]
  [[ "$output" == *"ACTION_UPGRADE"* ]]
}

@test "uninstalling that settles into failed re-runs preparation" {
  make_chart "temporal"
  write_stub helm '
STATE="$BATS_TEST_TMPDIR/seen"
case "$1" in
  status) if [ -f "$STATE" ]; then echo "{\"info\":{\"status\":\"failed\"}}"; else : > "$STATE"; echo "{\"info\":{\"status\":\"uninstalling\"}}"; fi ;;
  history) echo "[{\"revision\":5,\"status\":\"deployed\"}]" ;;
  rollback) echo "ACTION_ROLLBACK $*" >&2 ;;
  upgrade) echo "ACTION_UPGRADE" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [[ "$output" == *"re-running preparation"* ]]
  [[ "$output" == *"ACTION_ROLLBACK rollback temporal 5"* ]]
}

# --- unexpected status --------------------------------------------------------

@test "unexpected helm status aborts" {
  make_chart "temporal"
  helm_stub "some-weird-status" "[]"
  run_deploy "temporal" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"unexpected status"* ]]
}

@test "absent release (real helm not-found) deploys as first install" {
  make_chart "temporal"
  helm_stub_absent
  run_deploy "temporal" sit
  [ "$status" -eq 0 ]
  [[ "$output" == *"needs no preparation"* ]]
  [[ "$output" == *"ACTION_UPGRADE"* ]]
  [[ "$output" == *"Successfully deployed"* ]]
}

@test "status error that is NOT 'release: not found' aborts (no deploy)" {
  make_chart "temporal"
  helm_stub_api_error
  run_deploy "temporal" dev
  [ "$status" -ne 0 ]
  [[ "$output" == *"Could not read Helm status"* ]]
  [[ "$output" != *"ACTION_UPGRADE"* ]]
  [[ "$output" != *"ACTION_UNINSTALL"* ]]
}

@test "post-deploy verification fails the job if final status is not 'deployed'" {
  make_chart "temporal"
  # Pre-deploy status 'deployed' (no recovery), upgrade succeeds, but the final
  # verification read comes back 'failed' -> job must fail, not report healthy.
  write_stub helm '
UP="$BATS_TEST_TMPDIR/upgraded"
case "$1" in
  status)
    if [ -f "$UP" ]; then echo "{\"info\":{\"status\":\"failed\"}}"; else echo "{\"info\":{\"status\":\"deployed\"}}"; fi ;;
  history) echo "[{\"revision\":1,\"status\":\"deployed\"}]" ;;
  upgrade) echo "ACTION_UPGRADE" >&2; : > "$UP" ;;
  rollback) echo "ACTION_ROLLBACK $*" >&2 ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [ "$status" -ne 0 ]
  [[ "$output" == *"expected 'deployed'"* ]]
}

@test "post-deploy verification fails the job if final status read errors" {
  make_chart "temporal"
  write_stub helm '
UP="$BATS_TEST_TMPDIR/upgraded"
case "$1" in
  status)
    if [ -f "$UP" ]; then echo "Error: query failed: forbidden" >&2; exit 1; else echo "{\"info\":{\"status\":\"deployed\"}}"; fi ;;
  history) echo "[{\"revision\":1,\"status\":\"deployed\"}]" ;;
  upgrade) echo "ACTION_UPGRADE" >&2; : > "$UP" ;;
esac
exit 0
'
  run_deploy "temporal" sit
  [ "$status" -ne 0 ]
  [[ "$output" == *"could not be verified"* ]]
}
