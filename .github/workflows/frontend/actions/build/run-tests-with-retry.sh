#!/usr/bin/env bash
set -u

exit_code=0
yarn test:ci --concurrency=4 || exit_code=$?

if [[ $exit_code -eq 1 ]]; then
  echo "::warning::First attempt of testing the projects failed with exit code 1, retrying..."
  yarn test:ci --concurrency=4
else
  exit "$exit_code"
fi
