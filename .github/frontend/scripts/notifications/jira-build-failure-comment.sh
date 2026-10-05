#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${EMAIL:-}" || -z "${API_TOKEN:-}" ]]; then
  echo "Missing Jira credentials — skipping failure comment."
  exit 0
fi

jira_key=$(printf '%s' "${BRANCH_NAME:-}" | sed -nE 's#.*(CTECH-[0-9]+)-JIRA-XX.*#\1#p' | tr '[:lower:]' '[:upper:]')
if [[ -z "$jira_key" ]]; then
  echo "Could not extract Jira key from branch '${BRANCH_NAME:-}' — skipping."
  exit 0
fi

auth=$(printf '%s' "$EMAIL:$API_TOKEN" | base64 -w 0)
url="https://${JIRA_DOMAIN}/rest/api/3/issue/${jira_key}/comment"
payload=$(jq -n \
  --arg run_url "$RUN_URL" \
  --arg pr_url "${PR_URL:-}" \
  --arg pr_num "${PR_NUMBER:-unknown}" \
  --arg branch "$BRANCH_NAME" \
  '{body:{type:"doc",version:1,content:[{type:"heading",attrs:{level:2},content:[{type:"text",text:"⚠️ CI Build Failed"}]},{type:"paragraph",content:[{type:"text",text:("The CI build failed for branch: "+$branch)}]},{type:"bulletList",content:[{type:"listItem",content:[{type:"paragraph",content:(if $pr_url!="" then [{type:"text",text:"Pull Request: "},{type:"text",text:("PR #"+$pr_num),marks:[{type:"link",attrs:{href:$pr_url}}]}] else [{type:"text",text:"Pull Request: not yet created or URL unavailable"}] end)}]},{type:"listItem",content:[{type:"paragraph",content:[{type:"text",text:"Actions run: "},{type:"text",text:"View failed run",marks:[{type:"link",attrs:{href:$run_url}}]}]}]}]},{type:"paragraph",content:[{type:"text",text:"Please review the failed run, fix the issue, and re-trigger the workflow."}]}]}}')

response=$(curl -sS --http1.1 -w "\n%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic ${auth}" \
  --data-raw "$payload" \
  "$url")
http_code=$(printf '%s\n' "$response" | tail -n1)
body=$(printf '%s\n' "$response" | sed '$d')
echo "HTTP Code: ${http_code}"
if [[ "$http_code" == "201" ]]; then
  echo "Build failure comment posted to Jira ticket ${jira_key}"
else
  echo "Failed to post Jira comment (status ${http_code}): $body"
  exit 1
fi
