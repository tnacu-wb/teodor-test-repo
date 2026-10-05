#!/usr/bin/env bash
set -euo pipefail

: "${TARGET_ENVIRONMENT:?TARGET_ENVIRONMENT must be set}"
: "${HEAD_SHA:?HEAD_SHA must be set}"

graph_ref="Opera-t15exh@${TARGET_ENVIRONMENT}"
schema_glob='graphql/src/apollo/subgraphs/*/schema/schema.graphql'

# Same push-range handling as publish-graphql-schemas.sh: on a multi-commit push,
# HEAD^ alone would only check schemas touched by the final commit, so an earlier
# schema change could be published without ever being compatibility-checked.
# BEFORE_SHA is github.event.before — absent on pull_request and workflow_dispatch
# runs, all zeroes on a branch's first push, unreachable after a force push — so
# fall back to the single-commit window. On a PR, github.sha is the merge commit
# and HEAD^ is the base tip, which already covers the whole PR.
base_sha="${BEFORE_SHA:-}"
if [[ -z "$base_sha" || "$base_sha" =~ ^0+$ ]] || ! git cat-file -e "${base_sha}^{commit}" 2>/dev/null; then
  base_sha="${HEAD_SHA}^"
fi
echo "Checking schema changes in range ${base_sha}..${HEAD_SHA}"


# Deletions are ignored, matching the standalone pipeline: its check step skips
# missing paths ("Skipping non-existent or invalid schema path") and no pipeline
# anywhere calls "rover subgraph delete". Removing a subgraph from Apollo remains
# a manual Studio operation. --diff-filter=AMR excludes D, so a deleted schema is
# simply not selected.
schemas=()
while IFS= read -r schema_path; do
  [[ -n "$schema_path" ]] && schemas+=("$schema_path")
done < <(git diff --diff-filter=AMR --name-only "$base_sha" "$HEAD_SHA" -- "$schema_glob")

if [[ ${#schemas[@]} -eq 0 ]]; then
  echo "There are no updated schemas in this change."
  exit 0
fi

for schema_path in "${schemas[@]}"; do
  schema=${schema_path#graphql/src/apollo/subgraphs/}
  schema=${schema%/schema/schema.graphql}
  echo "Checking schema: $schema ($schema_path) against $graph_ref"
  rover subgraph check "$graph_ref" --schema "$schema_path" --name "$schema"
done
