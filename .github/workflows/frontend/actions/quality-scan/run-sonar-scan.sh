#!/usr/bin/env bash
set -euo pipefail

: "${COMPONENT_PATH:?COMPONENT_PATH is required}"
: "${COMPONENT_NAME:?COMPONENT_NAME is required}"
: "${SONAR_CLI_VERSION:?SONAR_CLI_VERSION is required}"
: "${GITHUB_REF_NAME:?GITHUB_REF_NAME is required}"

cp -av yarn.lock "$COMPONENT_PATH/"
if [[ ! -f "$COMPONENT_PATH/coverage/lcov.info" ]]; then
  echo "Lcov file not found in component path. Check lcov.info download step."
  exit 1
fi

"sonar-scanner-${SONAR_CLI_VERSION}-linux/bin/sonar-scanner" \
  -Dsonar.organization=whitbread-eos \
  -Dsonar.projectBaseDir="$COMPONENT_PATH/" \
  -Dsonar.branch.name="$GITHUB_REF_NAME" \
  -Dsonar.projectKey="whitbread-eos_pi-front-end-applications_${COMPONENT_NAME}" \
  -Dsonar.pullrequest.github.summary_comment=false \
  -Dsonar.javascript.lcov.reportPaths=coverage/lcov.info
