#!/usr/bin/env bash
# pre-verify.sh
#
# Validates a module before import into the digital-monorepo.
# Checks that the module declares a Spring Boot 4.x parent.
#
# Usage:
#   ./scripts/pre-verify.sh <source-pom-path> <source-root-dir>
#
# Arguments:
#   source-pom-path   Path to the module's pom.xml file
#   source-root-dir   Root directory of the module source (used for javax scanning)
#
# Exit codes:
#   0   Pass — Spring Boot 4.x detected
#   1   Spring Boot version out of range (not 4.x.y)
#   2   javax.* namespace detected (implemented separately)
#   3   No Spring Boot parent found or POM cannot be parsed

set -euo pipefail

# ---------------------------------------------------------------------------
# Argument validation
# ---------------------------------------------------------------------------

if [[ $# -lt 2 ]]; then
  echo "ERROR: Missing arguments." >&2
  echo "Usage: $0 <source-pom-path> <source-root-dir>" >&2
  exit 3
fi

SOURCE_POM="$1"
SOURCE_ROOT="$2"

if [[ ! -f "$SOURCE_POM" ]]; then
  echo "ERROR: POM file not found: $SOURCE_POM" >&2
  echo "  Cannot parse Spring Boot parent declaration." >&2
  echo "  Expected parent groupId: org.springframework.boot" >&2
  echo "  Expected parent artifactId: spring-boot-starter-parent" >&2
  exit 3
fi

# Derive module name from POM directory for reporting.
MODULE_NAME="$(basename "$(dirname "$SOURCE_POM")")"

# ---------------------------------------------------------------------------
# Spring Boot parent version extraction
# ---------------------------------------------------------------------------
# We look for a <parent> block containing either:
#   - groupId = org.springframework.boot (standard Spring Boot parent)
#   - artifactId containing spring-cloud-microservice-parent (Whitbread custom)
#
# Strategy: extract the <parent>...</parent> block, then check for known
# Spring Boot parent indicators and pull the <version> element.
# ---------------------------------------------------------------------------

# Extract the <parent> block (handles multiline).
PARENT_BLOCK=$(sed -n '/<parent>/,/<\/parent>/p' "$SOURCE_POM" 2>/dev/null || true)

if [[ -z "$PARENT_BLOCK" ]]; then
  echo "FAIL: No <parent> block found in $SOURCE_POM" >&2
  echo "  Module: $MODULE_NAME" >&2
  echo "  Expected parent groupId: org.springframework.boot" >&2
  echo "  Expected parent artifactId: spring-boot-starter-parent" >&2
  exit 3
fi

# Check if the parent block references a known Spring Boot parent.
IS_SB_PARENT=false

# Check for standard Spring Boot starter parent
if echo "$PARENT_BLOCK" | grep -q '<groupId>org\.springframework\.boot</groupId>'; then
  IS_SB_PARENT=true
fi

# Check for Whitbread's custom spring-cloud-microservice-parent (also SB4-based)
if echo "$PARENT_BLOCK" | grep -q 'spring-cloud-microservice-parent'; then
  IS_SB_PARENT=true
fi

if [[ "$IS_SB_PARENT" != "true" ]]; then
  echo "FAIL: No Spring Boot parent declaration found in $SOURCE_POM" >&2
  echo "  Module: $MODULE_NAME" >&2
  echo "  Found parent groupId: $(echo "$PARENT_BLOCK" | sed -n 's/.*<groupId>\(.*\)<\/groupId>.*/\1/p' | head -1 | tr -d '[:space:]')" >&2
  echo "  Expected parent groupId: org.springframework.boot" >&2
  echo "  Expected parent artifactId: spring-boot-starter-parent" >&2
  exit 3
fi

# Extract the version from within the parent block.
SB_VERSION=$(echo "$PARENT_BLOCK" | sed -n 's/.*<version>\(.*\)<\/version>.*/\1/p' | head -1 | tr -d '[:space:]')

if [[ -z "$SB_VERSION" ]]; then
  echo "FAIL: Could not extract version from <parent> block in $SOURCE_POM" >&2
  echo "  Module: $MODULE_NAME" >&2
  echo "  The <parent> block does not contain a <version> element." >&2
  echo "  Expected parent groupId: org.springframework.boot" >&2
  echo "  Expected parent artifactId: spring-boot-starter-parent" >&2
  exit 3
fi

# ---------------------------------------------------------------------------
# Version range validation: accept 4.x.y where x,y >= 0
# ---------------------------------------------------------------------------
# Pattern: exactly "4.<non-negative-integer>.<non-negative-integer>"
# Reject pre-release qualifiers, snapshots, or anything that doesn't match.
# ---------------------------------------------------------------------------

if [[ "$SB_VERSION" =~ ^4\.([0-9]+)\.([0-9]+)$ ]]; then
  echo "PASS: Spring Boot version $SB_VERSION is in the accepted range (4.x.y)."
  echo "  Module: $MODULE_NAME"
else
  echo "FAIL: Spring Boot version is out of the accepted range." >&2
  echo "  Module: $MODULE_NAME" >&2
  echo "  Declared version: $SB_VERSION" >&2
  echo "  Required range: 4.0.0–4.x.y (where x,y are non-negative integers)" >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# javax.* namespace scanning (Java EE detection)
# ---------------------------------------------------------------------------
# Scans all .java files under the source root directory for Java EE javax.*
# imports that must be migrated to Jakarta EE before monorepo import.
#
# Banned (Java EE) packages:
#   javax.persistence, javax.servlet, javax.validation, javax.inject,
#   javax.enterprise, javax.ws.rs, javax.transaction, javax.annotation
#   (excluding javax.annotation.processing which is Java SE)
#
# Allowed (Java SE) packages — NOT flagged:
#   javax.annotation.processing, javax.crypto, javax.net, javax.xml, javax.sql
# ---------------------------------------------------------------------------

if [[ ! -d "$SOURCE_ROOT" ]]; then
  echo "ERROR: Source root directory not found: $SOURCE_ROOT" >&2
  exit 3
fi

# Build a grep pattern that matches banned javax imports on import lines.
# The pattern matches import statements for banned Java EE javax packages.
JAVAX_PATTERN='^\s*import\s+(static\s+)?javax\.(persistence|servlet|validation|inject|enterprise|ws\.rs|transaction|annotation)\b'

# Find all .java files and scan for banned imports.
# We use a temporary file to collect results for reporting.
JAVAX_RESULTS_FILE=$(mktemp)
JAVAX_PACKAGES_FILE=$(mktemp)
trap 'rm -f "$JAVAX_RESULTS_FILE" "$JAVAX_PACKAGES_FILE"' EXIT

# Find offending files, excluding javax.annotation.processing.
# Use process substitution to avoid subshell issues with the while loop.
while IFS= read -r -d '' java_file; do
  # Extract lines matching the banned pattern from this file.
  matched_lines=$(grep -E "$JAVAX_PATTERN" "$java_file" 2>/dev/null || true)
  if [[ -z "$matched_lines" ]]; then
    continue
  fi

  # Filter out javax.annotation.processing (Java SE — allowed).
  # First, remove lines that are javax.annotation.processing imports.
  filtered_lines=$(echo "$matched_lines" | grep -v 'javax\.annotation\.processing' || true)
  if [[ -z "$filtered_lines" ]]; then
    continue
  fi

  # Extract just the top-level banned javax package name from filtered lines.
  offending_packages=$(echo "$filtered_lines" | \
    grep -oE 'javax\.(persistence|servlet|validation|inject|enterprise|ws\.rs|transaction|annotation)' | \
    sort -u || true)

  if [[ -n "$offending_packages" ]]; then
    echo "$java_file" >> "$JAVAX_RESULTS_FILE"
    echo "$offending_packages" >> "$JAVAX_PACKAGES_FILE"
  fi
done < <(find "$SOURCE_ROOT" -name '*.java' -type f -print0 2>/dev/null)

# Check if any offending files were found.
if [[ -s "$JAVAX_RESULTS_FILE" ]]; then
  OFFENDING_COUNT=$(wc -l < "$JAVAX_RESULTS_FILE" | tr -d '[:space:]')

  # Collect all unique javax packages detected across all files.
  DETECTED_PACKAGES=$(sort -u "$JAVAX_PACKAGES_FILE" | tr '\n' ',' | sed 's/,$//' | sed 's/,/, /g')

  echo "FAIL: Java EE javax.* namespace usage detected." >&2
  echo "  Module: $MODULE_NAME" >&2
  echo "  Offending files: $OFFENDING_COUNT" >&2
  echo "  Detected packages: $DETECTED_PACKAGES" >&2
  echo "  These imports must be migrated to Jakarta EE (jakarta.*) before import." >&2
  exit 2
fi

echo "PASS: No Java EE javax.* namespace usage detected."
echo "  Module: $MODULE_NAME"
exit 0
