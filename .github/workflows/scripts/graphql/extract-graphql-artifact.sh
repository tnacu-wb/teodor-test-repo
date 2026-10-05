#!/usr/bin/env bash
set -euo pipefail

: "${ARTIFACT_PATH:?ARTIFACT_PATH must be set}"
: "${GRAPHQL_DIR:?GRAPHQL_DIR must be set}"

tar -xf "$ARTIFACT_PATH" -C "$GRAPHQL_DIR"
