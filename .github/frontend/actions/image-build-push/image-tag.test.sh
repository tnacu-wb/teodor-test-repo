#!/usr/bin/env bash
set -euo pipefail

script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
# shellcheck source-path=SCRIPTDIR
# shellcheck source=image-tag.sh
source "$script_dir/image-tag.sh"

fail() {
  printf 'FAIL: %s\n' "$1" >&2
  exit 1
}

assert_tag() {
  local ref_name=$1
  local expected=$2
  local actual

  actual=$(generate_frontend_image_tag \
    '1.0.0' \
    "$ref_name" \
    '0123456789abcdef0123456789abcdef01234567' \
    '1787925628')
  [[ "$actual" == "$expected" ]] || fail "$ref_name: expected '$expected', got '$actual'"
}

assert_tag \
  'main' \
  '1.0.0-develop-0123456789abcdef0123456789abcdef01234567-1787925628'
assert_tag \
  'gh-readonly-queue/main/pr-123' \
  '1.0.0-main-0123456789abcdef0123456789abcdef01234567-1787925628'
assert_tag \
  'release/pi-26-2-4' \
  '1.0.0-release-pi-26-2-4-0123456789abcdef0123456789abcdef01234567-1787925628'
assert_tag \
  'hotfix/release-26-2-4' \
  '1.0.0-hotfix-release-26-2-4-0123456789abcdef0123456789abcdef01234567-1787925628'
assert_tag \
  'perf/test' \
  '1.0.0-perf-test-0123456789abcdef0123456789abcdef01234567-1787925628'
assert_tag \
  'feature/example' \
  '1.0.0-feature-example-0123456789abcdef0123456789abcdef01234567-1787925628'

main_tag=$(generate_frontend_image_tag \
  '1.0.0' \
  'main' \
  '0123456789abcdef0123456789abcdef01234567' \
  '1787925628')
flux_develop_pattern='^.+[0-9]-develop-[a-fA-F0-9]{40}-[0-9]{10}$'
[[ "$main_tag" =~ $flux_develop_pattern ]] || fail "main tag does not match the Flux develop policy: '$main_tag'"

if generate_frontend_image_tag '1.0.0' 'main' 'not-a-commit-sha' '1787925628' >/dev/null 2>&1; then
  fail 'invalid commit SHA was accepted'
fi

if generate_frontend_image_tag '1.0.0' 'main' '0123456789abcdef0123456789abcdef01234567' '123' >/dev/null 2>&1; then
  fail 'invalid timestamp was accepted'
fi

semantic_tag=$(generate_frontend_image_tag \
  '1.0.0' \
  'main' \
  '0123456789abcdef0123456789abcdef01234567' \
  '1787925628' \
  '2.7.0')
[[ "$semantic_tag" == '2.7.0' ]] || fail "semantic tag: expected '2.7.0', got '$semantic_tag'"

if generate_frontend_image_tag \
  '1.0.0' \
  'main' \
  '0123456789abcdef0123456789abcdef01234567' \
  '1787925628' \
  'v2.7.0' >/dev/null 2>&1; then
  fail 'invalid semantic version was accepted'
fi

if generate_frontend_image_tag \
  '1.0.0' \
  'release/pi-26-2-4' \
  '0123456789abcdef0123456789abcdef01234567' \
  '1787925628' \
  '2.7.0' >/dev/null 2>&1; then
  fail 'semantic version override was accepted outside production main'
fi

printf 'Frontend image tag tests passed.\n'
