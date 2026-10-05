#!/usr/bin/env bash
set -euo pipefail

jira_key=$(printf '%s' "$BRANCH_NAME" | sed -nE 's#.*(CTECH-[0-9]+)-JIRA-XX.*#\1#p' | tr '[:lower:]' '[:upper:]')
if [[ -z "$jira_key" ]]; then
  echo "Could not extract Jira key from branch '$BRANCH_NAME' — skipping."
  exit 0
fi
if [[ -z "${EMAIL:-}" || -z "${API_TOKEN:-}" ]]; then
  echo "Missing Jira credentials — skipping."
  exit 0
fi

auth=$(printf '%s' "$EMAIL:$API_TOKEN" | base64 -w 0)
payload=""
if [[ -f .kiro-jira-comment.json ]] && jq empty .kiro-jira-comment.json 2>/dev/null; then
  payload=$(<.kiro-jira-comment.json)
fi

if [[ -z "$payload" ]]; then
  pr_body=$(gh api "repos/${REPO}/pulls/${PR_NUMBER}" --jq '.body // ""')
  pr_url=$(gh api "repos/${REPO}/pulls/${PR_NUMBER}" --jq '.html_url // ""')
  payload=$(jq -n --arg body "$pr_body" --arg pr "$pr_url" \
    '{body:{type:"doc",version:1,content:[{type:"heading",attrs:{level:2},content:[{type:"text",text:"PR Opened & Deployed"}]},{type:"paragraph",content:[{type:"text",text:"Pull Request: "},{type:"text",text:$pr,marks:[{type:"link",attrs:{href:$pr}}]}]},{type:"heading",attrs:{level:3},content:[{type:"text",text:"PR Description"}]},{type:"paragraph",content:[{type:"text",text:$body}]}]}}')
fi

response=$(curl -sS --http1.1 -w "\n%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic ${auth}" \
  --data-raw "$payload" \
  "https://${JIRA_DOMAIN}/rest/api/3/issue/${jira_key}/comment")
http_code=$(printf '%s\n' "$response" | tail -n1)
body=$(printf '%s\n' "$response" | sed '$d')
echo "HTTP Code: ${http_code}"
if [[ "$http_code" == "201" ]]; then
  echo "Posted Jira comment to ${jira_key}"
else
  echo "Failed (status ${http_code}): $body"
  exit 1
fi
