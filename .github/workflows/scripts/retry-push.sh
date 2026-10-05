#!/usr/bin/env bash
# retry-push.sh
#
# Retry wrapper for transient git/network operations (git push, git tag push).
#
# Wraps an arbitrary command in up to 3 attempts with exponential backoff between
# attempts, then fails hard if all attempts are exhausted. Intended for the
# push and tag-push steps of the main-branch versioning workflow, where a
# transient network or remote-side failure should be retried rather than
# aborting the versioning run (Requirement 13.3).
#
# Backoff schedule (attempts = 3, base delay = 2s):
#   attempt 1 fails -> sleep 2s   (2 * 2^0)
#   attempt 2 fails -> sleep 4s   (2 * 2^1)
#   attempt 3 fails -> hard failure (no sleep after the final attempt)
#
# The wrapped command is executed verbatim; on success the wrapper exits 0
# immediately without further attempts. On persistent failure it exits with the
# last command's exit status (or 1 if that status was 0).
#
# Usage:
#   retry-push.sh <command> [args...]
#
# Examples:
#   retry-push.sh git push origin main
#   retry-push.sh git push origin "refs/tags/${VERSION}"
#
# Inputs (env, optional — sensible defaults for CI):
#   RETRY_MAX_ATTEMPTS   Maximum number of attempts (default: 3)
#   RETRY_BASE_DELAY     Base backoff delay in seconds (default: 2)
#
# Exit status:
#   0   the wrapped command succeeded on some attempt
#   !=0 all attempts failed (last non-zero exit status, or 1)

set -euo pipefail

if [[ "$#" -eq 0 ]]; then
  echo "retry-push.sh: no command supplied" >&2
  echo "usage: retry-push.sh <command> [args...]" >&2
  exit 2
fi

max_attempts="${RETRY_MAX_ATTEMPTS:-3}"
base_delay="${RETRY_BASE_DELAY:-2}"

if ! [[ "$max_attempts" =~ ^[0-9]+$ ]] || [[ "$max_attempts" -lt 1 ]]; then
  echo "retry-push.sh: RETRY_MAX_ATTEMPTS must be a positive integer (got '$max_attempts')" >&2
  exit 2
fi
if ! [[ "$base_delay" =~ ^[0-9]+$ ]]; then
  echo "retry-push.sh: RETRY_BASE_DELAY must be a non-negative integer (got '$base_delay')" >&2
  exit 2
fi

attempt=1
status=0

while [[ "$attempt" -le "$max_attempts" ]]; do
  echo "retry-push.sh: attempt ${attempt}/${max_attempts}: $*"

  # Run the wrapped command without letting a non-zero status abort the script
  # (set -e is temporarily disabled around the invocation).
  set +e
  "$@"
  status=$?
  set -e

  if [[ "$status" -eq 0 ]]; then
    echo "retry-push.sh: succeeded on attempt ${attempt}/${max_attempts}"
    exit 0
  fi

  echo "retry-push.sh: attempt ${attempt}/${max_attempts} failed with exit status ${status}" >&2

  if [[ "$attempt" -lt "$max_attempts" ]]; then
    # Exponential backoff: base_delay * 2^(attempt-1)
    delay=$(( base_delay * (2 ** (attempt - 1)) ))
    echo "retry-push.sh: backing off ${delay}s before retry" >&2
    sleep "$delay"
  fi

  attempt=$(( attempt + 1 ))
done

echo "retry-push.sh: all ${max_attempts} attempts failed; giving up" >&2
# Never report success on exhaustion, even if the last status was somehow 0.
if [[ "$status" -eq 0 ]]; then
  status=1
fi
exit "$status"
