#!/usr/bin/env bash
set -euo pipefail

current_path=$(pwd)

# scripts/runtimeEnvVars has no third-party dependencies - it runs on Node built-ins.

for app in business-booker ccui premier-inn; do
  scripts/runtimeEnvVars/index.mjs generate \
    --working-dir="$current_path/apps/next-apps/$app/" \
    --env-file-path="$current_path/apps/next-apps/$app/.env.production"
done
