# -------------------------------------------------
# Promo Service - JMeter Local Run Script (Windows)
# -------------------------------------------------

# Fail fast if JMETER_HOME is not set
if (-not $env:JMETER_HOME) {
    Write-Error "JMETER_HOME environment variable is not set."
    Write-Error "Please set JMETER_HOME to your JMeter installation directory."
    exit 1
}

# JMeter binary
$JMETER_BIN = "$env:JMETER_HOME\bin\jmeter.bat"

# Resolve script location dynamically
$SCRIPT_DIR = Split-Path -Parent $MyInvocation.MyCommand.Path

# Resolve JMeter base directory inside repo
$JMETER_BASE_DIR = Resolve-Path "$SCRIPT_DIR\..\jmeter"

# Files & folders
$JMX_FILE = "$JMETER_BASE_DIR\profiles\promo_batch_generation.jmx"

$BASE_RESULTS_DIR = "$JMETER_BASE_DIR\results\promo_batch_generation"
$RESULTS_FILE = "$BASE_RESULTS_DIR\results.jtl"
$HTML_REPORT_DIR = "$BASE_RESULTS_DIR\html-report"
$LOG_FILE = "$JMETER_BASE_DIR\jmeter.log"

# -------------------------------------------------
# Base properties (can be changed before running)
# -------------------------------------------------
$BASE_HOST = "localhost"
$BASE_PORT = "9137"
$BASE_PROTOCOL = "http"
$BASE_PATH = "/api/v1/promo/batches"

# -------------------------------------------------
# Prepare directories
# -------------------------------------------------
New-Item -ItemType Directory -Force -Path $BASE_RESULTS_DIR | Out-Null

if (Test-Path $HTML_REPORT_DIR) {
    Remove-Item -Recurse -Force $HTML_REPORT_DIR
}
New-Item -ItemType Directory -Force -Path $HTML_REPORT_DIR | Out-Null

# Clear old results
if (Test-Path $RESULTS_FILE) {
    Remove-Item $RESULTS_FILE -Force
    Write-Host "Old results.jtl deleted"
}

# Set working directory (important for relative paths)
Set-Location $JMETER_BASE_DIR

Write-Host "================================================="
Write-Host "Running JMeter Performance Test - Promo Service"
Write-Host "JMETER_HOME : $env:JMETER_HOME"
Write-Host "JMX File    : $JMX_FILE"
Write-Host "Results Dir : $BASE_RESULTS_DIR"
Write-Host "Base Host   : $BASE_HOST"
Write-Host "Base Port   : $BASE_PORT"
Write-Host "Base Path   : $BASE_PATH"
Write-Host "================================================="

# -------------------------------------------------
# Run JMeter in non-GUI mode with base properties
# -------------------------------------------------
& $JMETER_BIN `
  -n `
  -t $JMX_FILE `
  -l $RESULTS_FILE `
  -e -o $HTML_REPORT_DIR `
  -j $LOG_FILE `
  -JBASE_HOST="$BASE_HOST" `
  -JBASE_PORT="$BASE_PORT" `
  -JBASE_PROTOCOL="$BASE_PROTOCOL" `
  -JBASE_PATH="$BASE_PATH" `
  -JJMETER_BASE_DIR="$JMETER_BASE_DIR"

Write-Host "================================================="
Write-Host "JMeter test execution completed"
Write-Host "HTML Report:"
Write-Host "$HTML_REPORT_DIR\index.html"
Write-Host "================================================="
