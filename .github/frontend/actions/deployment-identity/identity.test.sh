#!/usr/bin/env bash
set -euo pipefail

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

run_identity() {
  local branch_name=$1
  local app_name=${2:-business-booker}
  local values_variant=${3:-}
  local output_file="$test_dir/output"

  : > "$output_file"
  # Built as an array so an unset variant exercises the script's own default
  # rather than passing VALUES_VARIANT="".
  local env_vars=(
    "BRANCH_NAME=$branch_name"
    "APP_NAME=$app_name"
    "PR_NUMBER=203"
    "GITHUB_OUTPUT=$output_file"
  )
  [[ -n "$values_variant" ]] && env_vars+=("VALUES_VARIANT=$values_variant")

  env "${env_vars[@]}" bash "$script_dir/identity.sh"

  printf '%s' "$output_file"
}

assert_output() {
  local output_file=$1
  local key=$2
  local expected=$3
  local actual

  actual=$(read_output "$output_file" "$key")
  [[ "$actual" == "$expected" ]] || fail "$key: expected '$expected', got '$actual'"
}

assert_dns_label() {
  local value=$1

  [[ ${#value} -le 63 ]] || fail "DNS label exceeds 63 characters: '$value'"
  [[ "$value" =~ ^[a-z0-9]([-a-z0-9]*[a-z0-9])?$ ]] || fail "Invalid DNS label: '$value'"
}

output_file=$(run_identity 'Feature/Foo_Bar')
assert_output "$output_file" sanitized-branch feature-foo-bar
assert_output "$output_file" release-name business-booker-pr-203
assert_output "$output_file" host-name business-booker-pr-203.business.dev.premierinn.digital
assert_dns_label "$(read_output "$output_file" release-name)"

output_file=$(run_identity '___')
assert_output "$output_file" sanitized-branch pr
assert_output "$output_file" release-name business-booker-pr-203

output_file=$(run_identity 'Feat/abcdefghijklmnopqrstuvwxyz123456789---')
sanitized_branch=$(read_output "$output_file" sanitized-branch)
[[ ${#sanitized_branch} -le 35 ]] || fail "Sanitized branch exceeds 35 characters: '$sanitized_branch'"
assert_dns_label "$sanitized_branch"
assert_dns_label "$(read_output "$output_file" release-name)"

# The chart and values files the deploy resolves to have to exist in the tree:
# a stale path here fails at `helm upgrade` time, one job into a deploy matrix,
# with an error that reads like a chart problem.
repo_root=$(cd "$script_dir/../../../.." && pwd)

output_file=$(run_identity 'feature/CTECH-1-thing' premier-inn)
assert_output "$output_file" chart-path infra/helm/opera-app-chart
assert_output "$output_file" values-variant ephemeral
assert_output "$output_file" values-path \
  frontend/pi-front-end-applications/apps/next-apps/premier-inn/helm/ephemeral-values.yaml

for app_name in business-booker ccui premier-inn; do
  for variant in ephemeral dev; do
    output_file=$(run_identity 'feature/CTECH-1-thing' "$app_name" "$variant")
    assert_output "$output_file" values-variant "$variant"

    chart_path=$(read_output "$output_file" chart-path)
    [[ -f "$repo_root/$chart_path/Chart.yaml" ]] \
      || fail "Chart does not exist: $chart_path"

    values_path=$(read_output "$output_file" values-path)
    [[ -f "$repo_root/$values_path" ]] \
      || fail "Values file does not exist: $values_path"
  done
done

# The dev (long-lived) variant keeps branch-based release naming.
output_file=$(run_identity 'feature/CTECH-1-thing' premier-inn dev)
assert_output "$output_file" release-name premier-inn-feature-ctech-1-thing

if env BRANCH_NAME='feature/CTECH-1-thing' APP_NAME=premier-inn PR_NUMBER=203 \
  VALUES_VARIANT=staging GITHUB_OUTPUT="$test_dir/rejected" \
  bash "$script_dir/identity.sh" >/dev/null 2>&1; then
  fail 'Unsupported values variant was accepted'
fi

printf 'Deployment identity tests passed.\n'
