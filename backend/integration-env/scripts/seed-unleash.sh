#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INTEGRATION_ENV_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
UNLEASH_DIR="$INTEGRATION_ENV_DIR/unleash"
UNLEASH_IMPORT_DIR="$UNLEASH_DIR/import"
FEATURES_FILE="$UNLEASH_DIR/client-features.json"
IMPORT_FILE="$UNLEASH_IMPORT_DIR/client-features.import.json"

REAL_UNLEASH_URL="${REAL_UNLEASH_URL:-https://eu.app.unleash-hosted.com/eukk0023/api}"
LOCAL_UNLEASH_PROJECT="${LOCAL_UNLEASH_PROJECT:-default}"
LOCAL_UNLEASH_IMPORT_ENVIRONMENT="${LOCAL_UNLEASH_IMPORT_ENVIRONMENT:-development}"

mkdir -p "$UNLEASH_DIR" "$UNLEASH_IMPORT_DIR"

if ! command -v jq >/dev/null 2>&1; then
  echo "ERROR: jq is required to generate the Unleash import file." >&2
  exit 1
fi

if [[ -n "${REAL_UNLEASH_TOKEN:-}" ]]; then
  echo "==> Fetching Unleash client features from $REAL_UNLEASH_URL/client/features..."
  if ! curl -fsS \
    --request GET \
    --url "$REAL_UNLEASH_URL/client/features" \
    --header "accept: application/json" \
    --header "authorization: $REAL_UNLEASH_TOKEN" \
    --output "$FEATURES_FILE.tmp"; then
    rm -f "$FEATURES_FILE.tmp"
    echo "ERROR: Failed to fetch Unleash client features. Check VPN/proxy access and REAL_UNLEASH_TOKEN." >&2
    exit 1
  fi
  mv "$FEATURES_FILE.tmp" "$FEATURES_FILE"
elif [[ -f "$FEATURES_FILE" ]]; then
  echo "==> REAL_UNLEASH_TOKEN is not set; reusing existing Unleash snapshot at $FEATURES_FILE"
else
  echo "ERROR: REAL_UNLEASH_TOKEN is not set and no local Unleash snapshot exists." >&2
  echo "       Export the DIT Unleash client token before running ./backend/integration-env/scripts/build.sh." >&2
  echo "       Example: REAL_UNLEASH_TOKEN='<token>' ./backend/integration-env/scripts/build.sh" >&2
  exit 1
fi

if ! jq -e '.features | type == "array"' "$FEATURES_FILE" >/dev/null; then
  echo "ERROR: $FEATURES_FILE does not look like an Unleash /client/features response." >&2
  exit 1
fi

echo "==> Generating Unleash startup import for project '$LOCAL_UNLEASH_PROJECT' and environment '$LOCAL_UNLEASH_IMPORT_ENVIRONMENT'..."
jq \
  --arg project "$LOCAL_UNLEASH_PROJECT" \
  --arg environment "$LOCAL_UNLEASH_IMPORT_ENVIRONMENT" \
  '
  (.segments // []) as $allSegments
  | def segment_constraints($ids):
      [($ids // [])[] as $id
        | $allSegments[]
        | select(.id == $id)
        | (.constraints // [])[]];
  {
    features: [
      .features[]
      | {
          name: .name,
          type: (.type // "release"),
          description: (.description // null),
          project: $project,
          stale: (.stale // false),
          impressionData: (.impressionData // false),
          archived: false
        }
    ],
    featureStrategies: [
      .features[] as $feature
      | ($feature.strategies // [])[]
      | {
          name: .name,
          featureName: $feature.name,
          title: (.title // null),
          parameters: ((.parameters // {}) | with_entries(.value |= tostring)),
          constraints: ((.constraints // []) + segment_constraints(.segments)),
          segments: [],
          variants: (.variants // []),
          disabled: (.disabled // false)
        }
    ],
    featureEnvironments: [
      .features[]
      | {
          name: .name,
          featureName: .name,
          environment: $environment,
          enabled: (.enabled // false),
          variants: (.variants // [])
        }
    ],
    contextFields: (
      [.features[] | .strategies[]?.constraints[]?.contextName | select(. != null)]
      | unique
      | map({
          name: .,
          description: "Imported from the DIT Unleash client snapshot",
          stickiness: (. == "userId" or . == "sessionId"),
          sortOrder: 999,
          legalValues: []
        })
    ),
    featureTags: [],
    segments: [],
    tagTypes: []
  }
  ' "$FEATURES_FILE" > "$IMPORT_FILE.tmp"

mv "$IMPORT_FILE.tmp" "$IMPORT_FILE"

features_count="$(jq '.features | length' "$IMPORT_FILE")"
strategies_count="$(jq '.featureStrategies | length' "$IMPORT_FILE")"
enabled_count="$(jq '[.featureEnvironments[] | select(.enabled == true)] | length' "$IMPORT_FILE")"

echo "==> Wrote Unleash snapshot to $FEATURES_FILE"
echo "==> Wrote Unleash import to $IMPORT_FILE"
echo "    Features: $features_count"
echo "    Strategies: $strategies_count"
echo "    Enabled in imported environment: $enabled_count"
