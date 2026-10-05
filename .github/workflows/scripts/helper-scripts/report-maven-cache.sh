#!/usr/bin/env bash
# report-maven-cache.sh
#
# Reports Maven cache effectiveness by analyzing the Maven build log.
# Generates a GitHub Actions step summary with metrics about:
#   - Artifacts downloaded during the build
#   - Local repository size
#   - Modules restored from build cache
#   - Modules saved to build cache
#   - EFS build cache size and entry count
#
# Inputs (env):
#   SERVICE_NAME      Name of the service being built
#   MVN_LOG_FILE      Path to Maven build log file (default: /tmp/mvn.log)
#   GITHUB_STEP_SUMMARY Path to GitHub Actions step summary file
#
# Outputs:
#   Writes formatted metrics to stdout and $GITHUB_STEP_SUMMARY

set -euo pipefail

: "${SERVICE_NAME:?SERVICE_NAME must be set}"
: "${GITHUB_STEP_SUMMARY:?GITHUB_STEP_SUMMARY must be set}"

MVN_LOG_FILE="${MVN_LOG_FILE:-/tmp/mvn.log}"

# grep -c prints the count AND exits 1 when it is zero, so `|| echo 0`
# would append a second line and break the arithmetic below.
downloads=$(grep -c "^Downloaded from" "$MVN_LOG_FILE" 2>/dev/null || true)
downloads=${downloads:-0}

bytes=$(du -sm ~/.m2/repository 2>/dev/null | cut -f1 || true)
bytes=${bytes:-0}

# Build cache stats: restores/saves from this build's log, plus the live
# size and entry count of the shared cache on the EFS volume.
restores=$(grep -c "Found cached build, restoring" "$MVN_LOG_FILE" 2>/dev/null || true)
restores=${restores:-0}

saves=$(grep -c "Saved Build to local file" "$MVN_LOG_FILE" 2>/dev/null || true)
saves=${saves:-0}

cache_mb=$(du -sm /mnt/efs/maven-build-cache 2>/dev/null | cut -f1 || true)
cache_mb=${cache_mb:-0}

cache_entries=$(find /mnt/efs/maven-build-cache -name buildinfo.xml 2>/dev/null | wc -l | tr -d ' ')

# Print to stdout
echo "artifacts downloaded during this build: $downloads"
echo "local repository size after build: ${bytes}MB"
echo "build cache: $restores module(s) restored, $saves saved; EFS cache ${cache_mb}MB across ${cache_entries} entries"

# Write to step summary
{
  echo "### Maven cache — $SERVICE_NAME"
  echo ""
  echo "| metric | value |"
  echo "|---|---|"
  echo "| artifacts downloaded | $downloads |"
  echo "| repository size | ${bytes}MB |"
  echo "| modules restored from build cache | $restores |"
  echo "| modules saved to build cache | $saves |"
  echo "| EFS build cache size | ${cache_mb}MB |"
  echo "| EFS build cache entries | $cache_entries |"
  echo ""
  if [[ "$downloads" -eq 0 ]]; then
    echo "Seeded cache was complete for this service."
  else
    echo "Residual tail: $downloads artifact(s) the seed job did not resolve."
  fi
} >> "$GITHUB_STEP_SUMMARY"
