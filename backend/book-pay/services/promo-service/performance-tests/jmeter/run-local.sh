#!/bin/bash
# -------------------------------------------------
# Promo Service - JMeter Local Run Script (Mac/Linux)
# -------------------------------------------------

# Fail fast if JMETER_HOME is not set
if [ -z "$JMETER_HOME" ]; then
  echo "ERROR: JMETER_HOME environment variable is not set."
  echo "Please set JMETER_HOME to your JMeter installation directory."
  exit 1
fi

# JMeter binary
JMETER_BIN="$JMETER_HOME/bin/jmeter"

# Resolve script location dynamically
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Resolve JMeter base directory inside repo
JMETER_BASE_DIR="$(cd "$SCRIPT_DIR/../jmeter" && pwd)"

# Files & folders
JMX_FILE="$JMETER_BASE_DIR/profiles/promo_batch_generation.jmx"

BASE_RESULTS_DIR="$JMETER_BASE_DIR/results/promo_batch_generation"
RESULTS_FILE="$BASE_RESULTS_DIR/results.jtl"
HTML_REPORT_DIR="$BASE_RESULTS_DIR/html-report"
LOG_FILE="$JMETER_BASE_DIR/jmeter.log"

# -------------------------------------------------
# Base properties (can be changed before running)
# -------------------------------------------------
BASE_HOST="localhost"
BASE_PORT="9137"
BASE_PROTOCOL="http"
BASE_PATH="/api/v1/promo/batches"

# -------------------------------------------------
# Prepare directories
# -------------------------------------------------
mkdir -p "$BASE_RESULTS_DIR"

if [ -d "$HTML_REPORT_DIR" ]; then
  rm -rf "$HTML_REPORT_DIR"
fi
mkdir -p "$HTML_REPORT_DIR"

# Clear old results
if [ -f "$RESULTS_FILE" ]; then
  rm -f "$RESULTS_FILE"
  echo "Old results.jtl deleted"
fi

# Set working directory (important for relative paths)
cd "$JMETER_BASE_DIR" || exit 1

echo "================================================="
echo "Running JMeter Performance Test - Promo Service"
echo "JMETER_HOME : $JMETER_HOME"
echo "JMX File    : $JMX_FILE"
echo "Results Dir : $BASE_RESULTS_DIR"
echo "Base Host   : $BASE_HOST"
echo "Base Port   : $BASE_PORT"
echo "Base Path   : $BASE_PATH"
echo "================================================="

# -------------------------------------------------
# Run JMeter in non-GUI mode with base properties
# -------------------------------------------------
"$JMETER_BIN" \
  -n \
  -t "$JMX_FILE" \
  -l "$RESULTS_FILE" \
  -e -o "$HTML_REPORT_DIR" \
  -j "$LOG_FILE" \
  -JBASE_HOST="$BASE_HOST" \
  -JBASE_PORT="$BASE_PORT" \
  -JBASE_PROTOCOL="$BASE_PROTOCOL" \
  -JBASE_PATH="$BASE_PATH" \
  -JJMETER_BASE_DIR="$JMETER_BASE_DIR"

echo "================================================="
echo "JMeter test execution completed"
echo "HTML Report:"
echo "$HTML_REPORT_DIR/index.html"
echo "================================================="
