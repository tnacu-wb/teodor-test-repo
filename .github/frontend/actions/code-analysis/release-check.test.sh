#!/usr/bin/env bash
set -euo pipefail

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
repo_root=${GITHUB_WORKSPACE:-$(cd "$script_dir/../../../.." && pwd)}
workflow_file="$repo_root/.github/workflows/ci-fe-deploy.yaml"

fail() {
  printf 'FAIL: %s\n' "$1" >&2
  exit 1
}

release_check_block=$(sed -n '/^  release-check:/,/^  dit-sync:/p' "$workflow_file")
[[ -n "$release_check_block" ]] || fail 'release-check job was not found'

assert_occurs_twice() {
  local expression=$1
  local description=$2
  local count

  count=$(grep -Fc -- "$expression" <<< "$release_check_block" || true)
  [[ "$count" -ge 2 ]] || fail "$description must be present in both the job condition and runner selection"
}

grep -Fq -- "github.event_name != 'pull_request'" <<< "$release_check_block" || fail 'pull requests must skip release-check'
grep -Fq -- "github.event_name != 'merge_group'" <<< "$release_check_block" || fail 'merge groups must skip release-check'
assert_occurs_twice "github.ref == 'refs/heads/main'" 'main branch support'
assert_occurs_twice "startsWith(github.ref, 'refs/heads/release/')" 'release branch support'
assert_occurs_twice "startsWith(github.ref, 'refs/heads/hotfix/release')" 'hotfix branch support'
assert_occurs_twice "startsWith(github.ref, 'refs/heads/perf/')" 'performance branch support'

printf 'Frontend release-check workflow tests passed.\n'
