#!/usr/bin/env bash
#
# Compute the actions/cache key and restore-key prefix for a repo's seen-advisories state.
#
# actions/cache keys can contain slashes, but matrix.repo (e.g. "kedacore/keda")
# reads more clearly here with a dash. GitHub Actions expressions have no
# replace()/regex function, so this is done in shell instead.
#
# actions/cache entries are immutable: saving to a key that already exists is a
# no-op, so the dedup state would never actually update if every run reused the
# same fixed key. Instead each run saves under a key suffixed with the run ID
# and attempt (always unique) and restores via a prefix match against
# `restore-keys`, which returns the most recently created cache with that
# prefix — i.e. the previous run's state.
#
# run_id alone is not unique across re-runs: a re-run keeps the same run_id and
# only increments run_attempt. Including run_attempt keeps the save key unique
# per attempt, so a re-run can still persist updated dedup state instead of
# silently no-opping against an existing key.
#
# Environment:
#   REPO         Matrix repo slug, e.g. "kedacore/keda". Required.
#   RUN_ID       github.run_id. Required.
#   RUN_ATTEMPT  github.run_attempt. Required.
#
# Outputs (written to $GITHUB_OUTPUT):
#   prefix  Restore-key prefix shared across runs for this repo.
#   key     Unique save key for this run attempt.

set -euo pipefail

prefix="security-advisories-seen-${REPO/\//-}-"
echo "prefix=${prefix}" >> "$GITHUB_OUTPUT"
echo "key=${prefix}${RUN_ID}-${RUN_ATTEMPT}" >> "$GITHUB_OUTPUT"
