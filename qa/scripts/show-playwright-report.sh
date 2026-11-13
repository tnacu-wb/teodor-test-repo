#!/bin/sh

set -eu

if [ -n "${APP:-}" ]; then
  report_dir="./reports/${APP}/html"
else
  report_dir=$(ls -td ./reports/*/html 2>/dev/null | head -n 1 || true)
fi

if [ -z "${report_dir:-}" ] || [ ! -d "$report_dir" ]; then
  echo "No Playwright HTML report was found. Set APP to the report application, for example APP=ccui." >&2
  exit 1
fi

echo "Opening Playwright report: $report_dir"
npx playwright show-report "$report_dir"
