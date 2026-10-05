#!/usr/bin/env bash
set -euo pipefail

for app in business-booker ccui premier-inn; do
  tar -cvf "$app.tar" \
    "apps/next-apps/$app/.env.production" \
    "apps/next-apps/$app/public" \
    "apps/next-apps/$app/.next/standalone" \
    "apps/next-apps/$app/.next/static" \
    scripts/runtimeEnvVars/
done
