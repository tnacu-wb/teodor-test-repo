#!/usr/bin/env bash
# Shared helpers for the infra/scripts bats suites.
#
# These tests exercise detect-charts.sh and deploy-chart.sh in isolation by:
#   * building a throwaway INFRA_DIR fixture with fake chart dirs, and
#   * putting stub `helm`/`kubectl`/`git` executables on PATH so no real
#     cluster or repo is ever touched.

# Absolute path to the scripts under test.
SCRIPTS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd -P)"

# Create an isolated working area: $BATS_TEST_TMPDIR/{infra,bin}.
# Sets INFRA_DIR and prepends the stub bin dir to PATH.
setup_workspace() {
  TEST_ROOT="$BATS_TEST_TMPDIR"
  INFRA_DIR="$TEST_ROOT/infra"
  STUB_BIN="$TEST_ROOT/bin"
  mkdir -p "$INFRA_DIR" "$STUB_BIN"
  export INFRA_DIR
  PATH="$STUB_BIN:$PATH"
  export PATH
}

# make_chart <relpath> [--ignore] [--values name1 name2 ...]
# Creates INFRA_DIR/<relpath>/Chart.yaml and optional markers/values files.
make_chart() {
  local rel="$1"; shift
  local dir="$INFRA_DIR/$rel"
  mkdir -p "$dir"
  printf 'apiVersion: v2\nname: %s\nversion: 0.0.0\n' "$(basename "$rel")" > "$dir/Chart.yaml"
  while [ $# -gt 0 ]; do
    case "$1" in
      --ignore) : > "$dir/.helm-deploy-ignore"; shift ;;
      --values)
        shift
        mkdir -p "$dir/values"
        while [ $# -gt 0 ] && [ "${1#--}" = "$1" ]; do
          printf '# %s\n' "$1" > "$dir/values/$1.yaml"; shift
        done
        ;;
      *) shift ;;
    esac
  done
}

# write_stub <name> <body>
# Creates an executable stub on PATH. The body is a bash script fragment that
# receives the stub's args as "$@".
write_stub() {
  local name="$1" body="$2"
  {
    echo '#!/usr/bin/env bash'
    echo "$body"
  } > "$STUB_BIN/$name"
  chmod +x "$STUB_BIN/$name"
}

# Convenience: a helm stub for an EXISTING release. Args:
#   helm_stub <status> <history-json>
# where <status> is returned by `helm status ... -o json` and <history-json> by
# `helm history ... -o json`. rollback/uninstall/upgrade log an ACTION_* marker
# and succeed. Use this only when the release exists; for an absent release use
# helm_stub_absent so the real Helm not-found behaviour is modelled.
helm_stub() {
  local status="$1" history="$2"
  write_stub helm "
case \"\$1\" in
  status)    echo '{\"info\":{\"status\":\"$status\"}}' ;;
  history)   echo '$history' ;;
  rollback)  echo \"ACTION_ROLLBACK \$*\" >&2 ;;
  uninstall) echo \"ACTION_UNINSTALL \$*\" >&2 ;;
  upgrade)   echo \"ACTION_UPGRADE \$*\" >&2 ;;
  *) : ;;
esac
exit 0
"
}

# helm stub for an ABSENT release, modelling real Helm 3: before any upgrade,
# `status` and `history` exit non-zero with 'Error: release: not found'. After
# an `upgrade` runs (marked via a state file), `status` reports 'deployed' so
# the script's post-deploy verification passes — mirroring a real first install.
# This exercises the not-found branches of release_status()/last_deployed_revision().
helm_stub_absent() {
  write_stub helm '
DEPLOYED="$BATS_TEST_TMPDIR/helm_deployed"
case "$1" in
  status)
    if [ -f "$DEPLOYED" ]; then echo "{\"info\":{\"status\":\"deployed\"}}"; else echo "Error: release: not found" >&2; exit 1; fi ;;
  history)
    if [ -f "$DEPLOYED" ]; then echo "[{\"revision\":1,\"status\":\"deployed\"}]"; else echo "Error: release: not found" >&2; exit 1; fi ;;
  upgrade)   echo "ACTION_UPGRADE $*" >&2; : > "$DEPLOYED" ;;
  rollback)  echo "ACTION_ROLLBACK $*" >&2 ;;
  uninstall) echo "ACTION_UNINSTALL $*" >&2 ;;
  *) : ;;
esac
exit 0
'
}

# helm stub that models a real, non-not-found API error on status/history (e.g.
# RBAC/unreachable): exits non-zero with an error that does NOT contain the
# 'release: not found' phrase. Callers must abort, never treat as absent.
helm_stub_api_error() {
  write_stub helm '
case "$1" in
  status|history) echo "Error: query: failed: namespaces \"x\" is forbidden" >&2; exit 1 ;;
  uninstall) echo "ACTION_UNINSTALL $*" >&2 ;;
  upgrade)   echo "ACTION_UPGRADE $*" >&2 ;;
esac
exit 0
'
}

# kubectl stub that always succeeds silently, and reports an empty PVC list
# (JSON) for `kubectl get pvc` (the common case: chart owns no persistent claims).
kubectl_stub() {
  write_stub kubectl '
case "$1" in
  get) if [ "$2" = "pvc" ]; then echo "{\"items\":[]}"; fi ;;
esac
exit 0
'
}

# kubectl stub that reports the release DOES own a PVC — via Helm's
# meta.helm.sh/release-name annotation (NOT the instance label), to exercise the
# broadened ownership detection. Release name is "temporal".
kubectl_stub_with_pvc() {
  write_stub kubectl '
case "$1" in
  get) if [ "$2" = "pvc" ]; then
    echo "{\"items\":[{\"metadata\":{\"name\":\"data-temporal-postgresql-0\",\"annotations\":{\"meta.helm.sh/release-name\":\"temporal\"}}}]}"
  fi ;;
esac
exit 0
'
}

# kubectl stub whose `get pvc` fails (RBAC/API error) so listing must fail closed.
kubectl_stub_pvc_error() {
  write_stub kubectl '
case "$1" in
  get) if [ "$2" = "pvc" ]; then echo "Error: forbidden" >&2; exit 1; fi ;;
esac
exit 0
'
}
