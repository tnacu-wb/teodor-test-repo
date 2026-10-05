#!/usr/bin/env bash
# ─── Allure Report Generator ─────────────────────────────────────────────────
# Generates an Allure HTML report from the test results for the specified app.
# Preserves history for trends, writes environment info, patches suite labels.
#
# Usage:
#   bash ./scripts/allure-report.sh              # Generate + open (default: pi)
#   bash ./scripts/allure-report.sh --no-open    # Generate without opening
#   APP=pib bash ./scripts/allure-report.sh      # Generate for PIB app

APP="${APP:-pi}"

# Always run from the qa/ directory
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
QA_DIR="$(dirname "$SCRIPT_DIR")"
cd "$QA_DIR"

RESULTS="./reports/${APP}/allure-results"
REPORT="./reports/${APP}/allure-report"

# Preserve history from previous report for trends
if [ -d "$REPORT/history" ]; then
  cp -r "$REPORT/history" "$RESULTS/history"
fi

# Write environment info for the Allure Environment widget
if [ "${EXECUTION_ENV:-lambdatest}" = "local" ]; then
  BROWSERS_RAN="${BROWSER:-chrome}"
else
  BROWSERS_RAN=$(grep -rh '"name"' "$RESULTS"/*.json 2>/dev/null | grep -oE '(chrome|firefox|edge|safari)' | sort -u | tr '\n' ', ' | sed 's/,$//' | sed 's/,/, /g')
  [ -z "$BROWSERS_RAN" ] && BROWSERS_RAN="Chrome, Firefox, Edge, Safari"
fi

cat > "$RESULTS/environment.properties" <<EOF
App=${APP}
Environment=${ENV:-uat}
Browser=${BROWSERS_RAN}
Viewport=${VIEWPORT:-desktop}
Execution=${EXECUTION_ENV:-lambdatest}
Node=$(node --version)
Playwright=$(npx playwright --version 2>/dev/null || echo 'N/A')
EOF

# Copy categories definition for failure classification
cp ./config/categories.json "$RESULTS/categories.json" 2>/dev/null || true

# Patch allure results: remap suite/behavior labels based on folder structure
# Maps: parentSuite=folder1, suite=folder2, subSuite=browser: describe
# Behaviors: feature from describe name or smoke/regression category
node -e "
const fs = require('fs'), path = require('path');
const dir = '$RESULTS';
if (!fs.existsSync(dir)) process.exit(0);
const files = fs.readdirSync(dir).filter(f => f.endsWith('-result.json'));
let n = 0;
for (const f of files) {
  try {
    const d = JSON.parse(fs.readFileSync(path.join(dir, f), 'utf-8'));
    const labels = d.labels || [];
    const suite = (labels.find(l => l.name === 'suite') || {}).value || '';
    const parts = suite.split('/').filter(Boolean);
    if (!parts[0]) continue;
    const orig = (labels.find(l => l.name === 'parentSuite') || {}).value || '';
    const browser = orig.replace('-lambdatest','').replace('chrome','Chrome').replace('firefox','Firefox').replace('edge','Edge').replace('safari','Safari');
    const sub = (labels.find(l => l.name === 'subSuite') || {}).value || '';
    d.labels = labels.filter(l => !['parentSuite','suite','subSuite','epic','feature','story'].includes(l.name));
    d.labels.push({ name: 'parentSuite', value: parts[0] });
    if (parts[1]) d.labels.push({ name: 'suite', value: parts[1] });
    const sv = browser && sub ? browser + ': ' + sub : (sub || browser || '');
    if (sv) d.labels.push({ name: 'subSuite', value: sv });
    const feat = parts[0] === 'smoke' ? 'Smoke' : parts[0] === 'regressions' ? 'Regression' : (sub || '');
    if (feat) d.labels.push({ name: 'feature', value: feat });
    fs.writeFileSync(path.join(dir, f), JSON.stringify(d, null, 2));
    n++;
  } catch (e) {}
}
console.log('  Patched ' + n + '/' + files.length + ' allure results');
"

npx allure generate "$RESULTS" --clean -o "$REPORT"

if [ "$1" != "--no-open" ]; then
  npx allure open "$REPORT"
fi
