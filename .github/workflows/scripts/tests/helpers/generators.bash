#!/usr/bin/env bash
# generators.bash
#
# Shared generator harness for the main-branch-versioning property tests
# (bats-core). Provides random-input generators used to drive the pure shell
# helpers across >=100 iterations per property, per the design's Testing
# Strategy.
#
# Consumers source this file from bats test files:
#   load "helpers/generators"
#
# Generators (each echoes one random record to stdout):
#   gen_semver              -> a random valid semantic version "X.Y.Z"
#   gen_semver_large        -> a random valid semver with large components
#   gen_tag_set [count]     -> newline-separated set of random version tags
#   gen_tag_run <ver> [n]   -> n consecutive minor tags starting at <ver>
#
# STATUS: STUB — scaffolded by task 1. Generator bodies are filled in by the
# property-test tasks (1.2, 1.3, 3.2). Each function currently returns a
# deterministic placeholder and is marked TODO so tests fail loudly until the
# real generators are implemented.

# --- Random primitives -----------------------------------------------------

# _rand_int <max> -> integer in [0, max)
_rand_int() {
  local max="${1:?_rand_int requires a max}"
  echo $(( RANDOM % max ))
}

# --- Semantic-version generators (P1, P2, P3) ------------------------------

# gen_semver -> random "X.Y.Z" with small components (0-99)
gen_semver() {
  : # TODO(task 1.2/3.2): echo "$(_rand_int 100).$(_rand_int 100).$(_rand_int 100)"
  echo "TODO_gen_semver"
}

# gen_semver_large -> random "X.Y.Z" incl. large components (edge cases)
gen_semver_large() {
  : # TODO(task 1.2): exercise large majors/minors/patches
  echo "TODO_gen_semver_large"
}

# gen_tag_set [count] -> newline-separated random existing-tag set
gen_tag_set() {
  : # TODO(task 1.3): build a set of random semver tags, size ~ [0, count]
  echo "TODO_gen_tag_set"
}

# gen_tag_run <start-version> [n] -> n consecutive minor tags from start
gen_tag_run() {
  : # TODO(task 1.3): emit X.Y.0, X.(Y+1).0, ... to exercise collision runs
  echo "TODO_gen_tag_run"
}
