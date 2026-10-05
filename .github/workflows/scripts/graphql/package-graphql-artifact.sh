#!/usr/bin/env bash
set -euo pipefail

: "${GRAPHQL_DIR:?GRAPHQL_DIR must be set}"
: "${ARTIFACT_PATH:?ARTIFACT_PATH must be set}"

tar -C "$GRAPHQL_DIR" -cvf "$ARTIFACT_PATH" .
