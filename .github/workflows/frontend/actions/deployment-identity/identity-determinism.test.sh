#!/usr/bin/env bash
set -euo pipefail

# Naming-determinism check for deployment-identity/identity.sh.
#
# The two ephemeral entry points (the POC `pull_request` gate and the
# `Ephemeral UP` comment trigger) must funnel into a single deploy workflow and
# produce the *same* Helm release, environment, and host names for a given PR —
# otherwise a comment on a POC branch would spin up a duplicate environment
# instead of upgrading the existing one, and cleanup (which keys off the same
# names) would miss releases.
#
# `identity.sh` derives those names purely from (app, branch, pr-number). It has
# no `trigger-source` (or equivalent) input. This harness proves that: it runs
# the *unchanged* script twice with identical (app, branch, pr-number) while
# injecting a different simulated `TRIGGER_SOURCE` each time (`pull_request` vs
# `comment`) and asserts release-name, environment-name, and host-name are
# byte-for-byte identical across both.

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
test_dir=$(mktemp -d)
trap 'rm -rf "$test_dir"' EXIT

fail() {
  printf 'FAIL: %s\n' "$1" >&2
  exit 1
}

read_output() {
  local file=$1
  local key=$2
  sed -n "s/^${key}=//p" "$file" | tail -n 1
}

# Run identity.sh with a fixed (app, branch, pr-number) and a simulated trigger
# source. TRIGGER_SOURCE is exported into the environment to mimic each entry
# point; identity.sh must ignore it entirely. Echoes the path of the captured
# GITHUB_OUTPUT file.
run_identity() {
  local app_name=$1
  local branch_name=$2
  local pr_number=$3
  local trigger_source=$4
  local output_file="$test_dir/output-${app_name}-${trigger_source}"

  : > "$output_file"
  env \
    "BRANCH_NAME=$branch_name" \
    "APP_NAME=$app_name" \
    "PR_NUMBER=$pr_number" \
    "TRIGGER_SOURCE=$trigger_source" \
    "GITHUB_OUTPUT=$output_file" \
    bash "$script_dir/identity.sh"

  printf '%s' "$output_file"
}

# Assert that a given output key is identical across the two trigger sources.
assert_independent() {
  local pull_request_file=$1
  local comment_file=$2
  local key=$3
  local pr_value comment_value

  pr_value=$(read_output "$pull_request_file" "$key")
  comment_value=$(read_output "$comment_file" "$key")

  [[ -n "$pr_value" ]] || fail "$key: empty for pull_request trigger source"
  [[ "$pr_value" == "$comment_value" ]] \
    || fail "$key depends on trigger source: pull_request='$pr_value', comment='$comment_value'"
}

# Fixed inputs. The branch mixes case and separators so we also exercise the
# sanitizer, and the PR number drives the environment name.
branch_name='Feature/CTECH-1234-On-Demand'
pr_number=203

for app_name in business-booker ccui premier-inn; do
  pull_request_file=$(run_identity "$app_name" "$branch_name" "$pr_number" pull_request)
  comment_file=$(run_identity "$app_name" "$branch_name" "$pr_number" comment)

  # The names both entry points must agree on (Requirements 4.5, 5.3).
  assert_independent "$pull_request_file" "$comment_file" release-name
  assert_independent "$pull_request_file" "$comment_file" environment-name
  assert_independent "$pull_request_file" "$comment_file" host-name

  # Ephemeral releases key on the PR number, not the branch (both entry points
  # converge on <app>-PR-<n>).
  expected_release="${app_name}-pr-${pr_number}"
  actual_release=$(read_output "$pull_request_file" release-name)
  [[ "$actual_release" == "$expected_release" ]] \
    || fail "$app_name release-name expected '$expected_release', got '$actual_release'"
  # host-name is derived from the release name.
  actual_host=$(read_output "$pull_request_file" host-name)
  [[ "$actual_host" == "$expected_release."* ]] \
    || fail "$app_name host-name '$actual_host' is not derived from release '$expected_release'"
done

# Belt-and-braces: derivation is a pure function of (app, branch, pr-number), so
# the same inputs must reproduce byte-for-byte regardless of how many times, or
# in what order, the two "entry points" run.
first=$(run_identity premier-inn "$branch_name" "$pr_number" comment)
second=$(run_identity premier-inn "$branch_name" "$pr_number" pull_request)
if ! diff <(sort "$first") <(sort "$second") >/dev/null; then
  fail 'identity.sh output differs across trigger sources for identical (app, branch, pr-number)'
fi

# Ephemeral-only guard: the long-lived dev release must stay branch-based, never
# <app>-PR-<n>. Run identity.sh with VALUES_VARIANT=dev and assert the release
# name is not the PR-based form.
dev_output="$test_dir/output-dev"
: > "$dev_output"
env \
  "BRANCH_NAME=$branch_name" \
  "APP_NAME=premier-inn" \
  "PR_NUMBER=$pr_number" \
  "VALUES_VARIANT=dev" \
  "GITHUB_OUTPUT=$dev_output" \
  bash "$script_dir/identity.sh"
dev_release=$(read_output "$dev_output" release-name)
if [[ "$dev_release" == "premier-inn-pr-${pr_number}" ]]; then
  fail "dev variant release-name must stay branch-based, got PR-based '$dev_release'"
fi
[[ -n "$dev_release" ]] || fail "dev variant release-name is empty"

printf 'Deployment identity naming-determinism tests passed.\n'
