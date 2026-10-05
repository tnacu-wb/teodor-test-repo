#!/usr/bin/env bats
#
# Tests for infra/scripts/detect-charts.sh

load test_helper

setup() {
  setup_workspace
  DETECT="$SCRIPTS_DIR/detect-charts.sh"
}

# --- workflow_dispatch --------------------------------------------------------

@test "dispatch: valid chart is emitted as a JSON array" {
  make_chart "temporal"
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=temporal "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=["temporal"]'* ]]
}

@test "dispatch: nested chart path is emitted verbatim" {
  make_chart "helm/some-app"
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=helm/some-app "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=["helm/some-app"]'* ]]
}

@test "dispatch: base/library chart (.helm-deploy-ignore) is rejected" {
  make_chart "helm/opera-app-chart" --ignore
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=helm/opera-app-chart "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"base/library chart"* ]]
}

@test "dispatch: empty input is rejected" {
  run env EVENT_NAME=workflow_dispatch INPUT_CHART= "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

@test "dispatch: path traversal (..) is rejected" {
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=../backend/x "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

@test "dispatch: absolute path is rejected" {
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=/etc/passwd "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

@test "dispatch: shell metacharacters are rejected" {
  run env EVENT_NAME=workflow_dispatch 'INPUT_CHART=temporal;touch pwned' "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

@test "dispatch: nonexistent chart is rejected" {
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=does-not-exist "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"does not resolve"* ]]
}

@test "dispatch: '.' path component is rejected" {
  make_chart "temporal"
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=temporal/. "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

@test "dispatch: bare '.' is rejected" {
  run env EVENT_NAME=workflow_dispatch INPUT_CHART=. "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"Invalid chart input"* ]]
}

# --- push ---------------------------------------------------------------------

# A git stub that reports a fixed changed-file list and treats any before-SHA as
# reachable. CHANGED_FILES env controls the diff output (newline-separated).
git_stub_ok() {
  write_stub git '
case "$1" in
  rev-parse) exit 0 ;;
  diff)      printf "%s\n" "$CHANGED_FILES" ;;
  *) : ;;
esac
exit 0
'
}

@test "push: changed file under a chart yields that chart" {
  make_chart "temporal"
  git_stub_ok
  run env EVENT_NAME=push BEFORE_SHA=aaa AFTER_SHA=bbb \
    CHANGED_FILES="$INFRA_DIR/temporal/values/dev.yaml" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=["temporal"]'* ]]
}

@test "push: nested-chart file maps to nearest Chart.yaml dir" {
  make_chart "helm/some-app"
  git_stub_ok
  run env EVENT_NAME=push BEFORE_SHA=aaa AFTER_SHA=bbb \
    CHANGED_FILES="$INFRA_DIR/helm/some-app/templates/deploy.yaml" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=["helm/some-app"]'* ]]
}

@test "push: base chart change is skipped" {
  make_chart "helm/opera-app-chart" --ignore
  git_stub_ok
  run env EVENT_NAME=push BEFORE_SHA=aaa AFTER_SHA=bbb \
    CHANGED_FILES="$INFRA_DIR/helm/opera-app-chart/values.yaml" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=[]'* ]]
}

@test "push: non-chart file (no Chart.yaml ancestor) yields empty" {
  git_stub_ok
  run env EVENT_NAME=push BEFORE_SHA=aaa AFTER_SHA=bbb \
    CHANGED_FILES="$INFRA_DIR/README.md" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=[]'* ]]
}

@test "push: multiple charts are de-duplicated and sorted" {
  make_chart "temporal"
  make_chart "other"
  git_stub_ok
  run env EVENT_NAME=push BEFORE_SHA=aaa AFTER_SHA=bbb \
    CHANGED_FILES="$(printf '%s/temporal/a.yaml\n%s/other/b.yaml\n%s/temporal/c.yaml' "$INFRA_DIR" "$INFRA_DIR" "$INFRA_DIR")" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *'charts=["other","temporal"]'* ]]
}

@test "push: unreachable base commit fails loudly (no silent skip)" {
  write_stub git '
case "$1" in
  rev-parse) exit 1 ;;   # base commit not found
  diff) printf "" ;;
esac
exit 0
'
  run env EVENT_NAME=push BEFORE_SHA=deadbeef AFTER_SHA=bbb "$DETECT"
  [ "$status" -ne 0 ]
  [[ "$output" == *"not found in history"* ]]
}

@test "push: zero SHA (branch creation) diffs against empty tree instead of failing" {
  make_chart "temporal"
  # git stub: hash-object returns a fake empty-tree id; rev-parse must NOT be
  # called for the zero SHA (test asserts we don't fail on it); diff returns the
  # changed files regardless of base.
  write_stub git '
case "$1" in
  hash-object) echo "4b825dc642cb6eb9a060e54bf8d69288fbee4904" ;;
  rev-parse)   echo "UNEXPECTED_REV_PARSE" >&2; exit 1 ;;
  diff)        printf "%s\n" "$CHANGED_FILES" ;;
esac
exit 0
'
  run env EVENT_NAME=push BEFORE_SHA=0000000000000000000000000000000000000000 \
    AFTER_SHA=bbb CHANGED_FILES="$INFRA_DIR/temporal/Chart.yaml" "$DETECT"
  [ "$status" -eq 0 ]
  [[ "$output" == *"empty tree"* ]]
  [[ "$output" == *'charts=["temporal"]'* ]]
  [[ "$output" != *"UNEXPECTED_REV_PARSE"* ]]
}
