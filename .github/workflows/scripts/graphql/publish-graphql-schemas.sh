#!/usr/bin/env bash
set -euo pipefail

: "${TARGET_ENVIRONMENT:?TARGET_ENVIRONMENT must be set}"
: "${HEAD_SHA:?HEAD_SHA must be set}"

graph_ref="Opera-t15exh@${TARGET_ENVIRONMENT}"
routing_url="http://opera-apollo-subgraphs.apollo.svc.cluster.local:4000/graphql"
schema_glob='graphql/src/apollo/subgraphs/*/schema/schema.graphql'

# Deletions are ignored, matching the standalone pipeline: its check step skips
# missing paths ("Skipping non-existent or invalid schema path") and no pipeline
# anywhere calls "rover subgraph delete". Removing a subgraph from Apollo remains
# a manual Studio operation. --diff-filter=AMR excludes D, so a deleted schema is
# simply not selected.
schemas=()
if [[ "$TARGET_ENVIRONMENT" == "DEV" ]]; then
  # Diff the whole push range so a multi-commit push cannot skip a schema that was
  # changed by an earlier commit in the same push. BEFORE_SHA is
  # github.event.before, which is all zeroes on a branch's first push and can be
  # unreachable after a force push; fall back to the single-commit window.
  base_sha="${BEFORE_SHA:-}"
  if [[ -z "$base_sha" || "$base_sha" =~ ^0+$ ]] || ! git cat-file -e "${base_sha}^{commit}" 2>/dev/null; then
    base_sha="${HEAD_SHA}^"
  fi
  echo "Comparing schema changes in range ${base_sha}..${HEAD_SHA}"

  while IFS= read -r schema_path; do
    [[ -n "$schema_path" ]] && schemas+=("$schema_path")
  done < <(git diff --diff-filter=AMR --name-only "$base_sha" "$HEAD_SHA" -- "$schema_glob")
else
  while IFS= read -r schema_path; do
    [[ -n "$schema_path" ]] && schemas+=("$schema_path")
  done < <(find graphql/src/apollo/subgraphs -name schema.graphql -type f | sort)
fi

if [[ ${#schemas[@]} -eq 0 ]]; then
  echo "There are no schemas to publish."
  exit 0
fi

for schema_path in "${schemas[@]}"; do
  schema=${schema_path#graphql/src/apollo/subgraphs/}
  schema=${schema%/schema/schema.graphql}
  echo "Publishing schema: $schema ($schema_path) to $graph_ref"
  rover subgraph publish "$graph_ref" \
    --name "$schema" \
    --schema "$schema_path" \
    --routing-url "$routing_url"
done
