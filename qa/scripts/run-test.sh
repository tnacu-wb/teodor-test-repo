#!/usr/bin/env bash
# ─── QA Test Runner ──────────────────────────────────────────────────────────
# Wrapper script for running Playwright tests with environment variables.
# By default runs on LambdaTest. Use --local to run on your machine.
#
# Usage:
#   ./scripts/run-test.sh                              # PI, UAT, Chrome, LambdaTest
#   ./scripts/run-test.sh --local                      # PI, UAT, Chrome, local browser
#   ./scripts/run-test.sh --app=pib --env=dit          # PIB, DIT, LambdaTest
#   ./scripts/run-test.sh --local --browser=firefox    # PI, UAT, Firefox, local
#   ./scripts/run-test.sh --grep=@smoke                # Smoke tests only on LambdaTest
#   ./scripts/run-test.sh -- tests/regressions/pi/     # Specific test path

set -euo pipefail

# Always run from the qa/ directory regardless of where the script is called from
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
QA_DIR="$(dirname "$SCRIPT_DIR")"
cd "$QA_DIR"

# Defaults (LambdaTest by default — use --local to override)
APP="${APP:-pi}"
ENV="${ENV:-uat}"
BROWSER="${BROWSER:-chrome}"
VIEWPORT="${VIEWPORT:-desktop}"
HEADED="${HEADED:-true}"
EXECUTION_ENV="${EXECUTION_ENV:-lambdatest}"

# Parse arguments
EXTRA_ARGS=()
for arg in "$@"; do
  case "$arg" in
    --local)       EXECUTION_ENV="local" ;;
    --app=*)       APP="${arg#*=}" ;;
    --env=*)       ENV="${arg#*=}" ;;
    --browser=*)   BROWSER="${arg#*=}" ;;
    --viewport=*)  VIEWPORT="${arg#*=}" ;;
    --headed)      HEADED="true" ;;
    --headless)    HEADED="false" ;;
    --grep=*)      GREP="${arg#*=}" ;;
    --)            shift; EXTRA_ARGS+=("$@"); break ;;
    *)             EXTRA_ARGS+=("$arg") ;;
  esac
done

# Export
export APP ENV BROWSER VIEWPORT HEADED EXECUTION_ENV
[ -n "${GREP:-}" ] && export GREP

echo "─── Running Tests ───────────────────────────────────────────"
echo "  APP:        $APP"
echo "  ENV:        $ENV"
if [ "$EXECUTION_ENV" = "local" ]; then
  echo "  BROWSER:    $BROWSER"
else
  echo "  BROWSERS:   Chrome, Firefox, Edge, Safari"
fi
echo "  VIEWPORT:   $VIEWPORT"
echo "  HEADED:     $HEADED"
echo "  EXECUTION:  $EXECUTION_ENV"
[ -n "${GREP:-}" ] && echo "  GREP:       $GREP"
echo "──────────────────────────────────────────────────────────────"

# Clean previous allure results (preserve history for trends)
RESULTS_DIR="./reports/${APP}/allure-results"
REPORT_DIR="./reports/${APP}/allure-report"
if [ -d "$REPORT_DIR/history" ]; then
  mkdir -p /tmp/allure-history-$$
  cp -r "$REPORT_DIR/history" /tmp/allure-history-$$/
fi
rm -rf "$RESULTS_DIR"
mkdir -p "$RESULTS_DIR"
if [ -d "/tmp/allure-history-$$/history" ]; then
  cp -r /tmp/allure-history-$$/history "$RESULTS_DIR/"
  rm -rf /tmp/allure-history-$$
fi

# Run tests (capture exit code but don't stop on failure)
npx playwright test ${EXTRA_ARGS[@]+"${EXTRA_ARGS[@]}"} || true

# Generate and open Allure report (always, regardless of test outcome)
echo ""
echo "─── Generating Allure Report ────────────────────────────────"
bash ./scripts/allure-report.sh --no-open

# Send Teams notification
npx tsx ./scripts/notify-teams.ts

# Open Allure report
echo ""
npx allure open "./reports/${APP}/allure-report"
