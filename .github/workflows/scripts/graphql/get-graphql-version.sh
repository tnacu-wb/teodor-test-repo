#!/usr/bin/env bash
set -euo pipefail

: "${GITHUB_OUTPUT:?GITHUB_OUTPUT must be set}"

version=$(npm pkg get version --workspaces=false | tr -d '"')
echo "Apollo version: $version"
echo "apollo-version=$version" >> "$GITHUB_OUTPUT"
