#!/usr/bin/env bash
# adapt-pom.sh
#
# Transforms a module POM to conform to monorepo conventions.
# This script handles:
#   - Parent block rewrite (to inherit from digital-monorepo root POM)
#   - Project-level <version> replacement with ${revision}
#   - Removal of project-level <groupId> if it matches uk.co.whitbread
#
# Usage:
#   ./scripts/adapt-pom.sh <module-pom-path> <module-type: library|service>
#
# Arguments:
#   module-pom-path   Path to the module's pom.xml file to adapt
#   module-type       Either "library" or "service"
#
# Exit codes:
#   0   Success
#   1   POM file not found or invalid arguments
#   2   POM manipulation failed

set -euo pipefail

# ---------------------------------------------------------------------------
# Argument validation
# ---------------------------------------------------------------------------

if [[ $# -lt 2 ]]; then
  echo "ERROR: Missing arguments." >&2
  echo "Usage: $0 <module-pom-path> <module-type: library|service>" >&2
  exit 1
fi

MODULE_POM="$1"
MODULE_TYPE="$2"

if [[ ! -f "$MODULE_POM" ]]; then
  echo "ERROR: POM file not found: $MODULE_POM" >&2
  exit 1
fi

if [[ "$MODULE_TYPE" != "library" && "$MODULE_TYPE" != "service" ]]; then
  echo "ERROR: Invalid module-type: $MODULE_TYPE (must be 'library' or 'service')" >&2
  exit 1
fi

# Derive module name from POM directory for reporting.
MODULE_NAME="$(basename "$(dirname "$MODULE_POM")")"

echo "Adapting POM: $MODULE_POM (type: $MODULE_TYPE, module: $MODULE_NAME)"

# ---------------------------------------------------------------------------
# Step 1: Replace the entire <parent>...</parent> block
# ---------------------------------------------------------------------------
# Uses awk to find the <parent> block and replace it wholesale with the
# monorepo parent declaration. This handles any indentation and content
# within the original parent block.
# ---------------------------------------------------------------------------

echo "  [1/3] Rewriting <parent> block..."

TEMP_FILE=$(mktemp)
trap 'rm -f "$TEMP_FILE"' EXIT

awk '
BEGIN { in_parent = 0; replaced = 0 }
/<parent>/ && !in_parent {
  in_parent = 1
  # Detect leading whitespace from the <parent> line for indentation
  match($0, /^[[:space:]]*/)
  indent = substr($0, RSTART, RLENGTH)
  next
}
in_parent && /<\/parent>/ {
  in_parent = 0
  replaced = 1
  # Output the new parent block with consistent indentation
  print indent "<parent>"
  print indent "    <groupId>uk.co.whitbread</groupId>"
  print indent "    <artifactId>digital-monorepo</artifactId>"
  print indent "    <version>${revision}</version>"
  print indent "    <relativePath>../../../pom.xml</relativePath>"
  print indent "</parent>"
  next
}
in_parent { next }  # Skip lines inside the old parent block
{ print }
END {
  if (!replaced) {
    print "ERROR: No <parent> block found in POM" > "/dev/stderr"
    exit 2
  }
}
' "$MODULE_POM" > "$TEMP_FILE"

if [[ $? -ne 0 ]]; then
  echo "ERROR: Failed to rewrite <parent> block in $MODULE_POM" >&2
  exit 2
fi

# Verify the temp file is non-empty and contains the new parent
if [[ ! -s "$TEMP_FILE" ]]; then
  echo "ERROR: Parent block rewrite produced empty output" >&2
  exit 2
fi

cp "$TEMP_FILE" "$MODULE_POM"

# ---------------------------------------------------------------------------
# Step 2: Replace the project-level <version> element with ${revision}
# ---------------------------------------------------------------------------
# The project-level <version> appears as a direct child of <project>,
# typically right after </parent> and before or after <artifactId>.
# We must NOT modify <version> elements inside:
#   - <parent> (already replaced)
#   - <dependency> entries
#   - <plugin> entries
#   - <dependencyManagement> entries
#
# Strategy: Use awk to track nesting depth. The project-level <version> is
# one that appears at depth 1 (direct child of <project>) and is NOT inside
# the <parent> block (which we already replaced with ${revision}).
# ---------------------------------------------------------------------------

echo "  [2/3] Replacing project-level <version> with \${revision}..."

awk '
BEGIN {
  depth = 0
  in_parent = 0
  in_dependencies = 0
  in_dependency_mgmt = 0
  in_build = 0
  in_plugins = 0
  in_plugin = 0
  in_dependency = 0
  version_replaced = 0
}

# Track entry into nested sections that contain their own <version> elements
/<parent>/ { in_parent = 1 }
/<\/parent>/ { in_parent = 0 }

/<dependencies>/ { in_dependencies++ }
/<\/dependencies>/ { if (in_dependencies > 0) in_dependencies-- }

/<dependencyManagement>/ { in_dependency_mgmt = 1 }
/<\/dependencyManagement>/ { in_dependency_mgmt = 0 }

/<build>/ { in_build = 1 }
/<\/build>/ { in_build = 0 }

/<plugins>/ { in_plugins++ }
/<\/plugins>/ { if (in_plugins > 0) in_plugins-- }

/<plugin>/ { in_plugin++ }
/<\/plugin>/ { if (in_plugin > 0) in_plugin-- }

/<dependency>/ { in_dependency++ }
/<\/dependency>/ { if (in_dependency > 0) in_dependency-- }

# Match <version>...</version> on a single line
/<version>.*<\/version>/ {
  # Only replace if we are at the project level:
  # NOT inside parent, dependencies, dependencyManagement, build/plugins, or plugin
  if (!in_parent && in_dependencies == 0 && !in_dependency_mgmt && in_plugins == 0 && in_plugin == 0 && in_dependency == 0 && !version_replaced) {
    # Replace the version value, preserving indentation
    sub(/<version>[^<]*<\/version>/, "<version>${revision}</version>")
    version_replaced = 1
  }
}

{ print }
' "$MODULE_POM" > "$TEMP_FILE"

if [[ $? -ne 0 ]]; then
  echo "ERROR: Failed to replace project-level <version> in $MODULE_POM" >&2
  exit 2
fi

cp "$TEMP_FILE" "$MODULE_POM"

# ---------------------------------------------------------------------------
# Step 3: Remove project-level <groupId> if it matches uk.co.whitbread
# ---------------------------------------------------------------------------
# The project-level <groupId> appears as a direct child of <project>,
# typically after </parent>. We only remove it if the value is exactly
# "uk.co.whitbread" — NOT "uk.co.whitbread.shared" or any other variant.
#
# Same nesting-aware strategy as Step 2.
# ---------------------------------------------------------------------------

echo "  [3/3] Checking project-level <groupId> for removal..."

awk '
BEGIN {
  in_parent = 0
  in_dependencies = 0
  in_dependency_mgmt = 0
  in_build = 0
  in_plugins = 0
  in_plugin = 0
  in_dependency = 0
  removed = 0
}

/<parent>/ { in_parent = 1 }
/<\/parent>/ { in_parent = 0 }

/<dependencies>/ { in_dependencies++ }
/<\/dependencies>/ { if (in_dependencies > 0) in_dependencies-- }

/<dependencyManagement>/ { in_dependency_mgmt = 1 }
/<\/dependencyManagement>/ { in_dependency_mgmt = 0 }

/<build>/ { in_build = 1 }
/<\/build>/ { in_build = 0 }

/<plugins>/ { in_plugins++ }
/<\/plugins>/ { if (in_plugins > 0) in_plugins-- }

/<plugin>/ { in_plugin++ }
/<\/plugin>/ { if (in_plugin > 0) in_plugin-- }

/<dependency>/ { in_dependency++ }
/<\/dependency>/ { if (in_dependency > 0) in_dependency-- }

# Match <groupId>uk.co.whitbread</groupId> exactly (not uk.co.whitbread.shared etc.)
/^[[:space:]]*<groupId>uk\.co\.whitbread<\/groupId>[[:space:]]*$/ {
  if (!in_parent && in_dependencies == 0 && !in_dependency_mgmt && in_plugins == 0 && in_plugin == 0 && in_dependency == 0 && !removed) {
    # Skip this line (remove it)
    removed = 1
    next
  }
}

{ print }

END {
  if (removed) {
    print "    Removed project-level <groupId>uk.co.whitbread</groupId> (inherited from parent)" > "/dev/stderr"
  } else {
    print "    Project-level <groupId> retained (does not match uk.co.whitbread or not present)" > "/dev/stderr"
  }
}
' "$MODULE_POM" > "$TEMP_FILE"

if [[ $? -ne 0 ]]; then
  echo "ERROR: Failed to process <groupId> removal in $MODULE_POM" >&2
  exit 2
fi

cp "$TEMP_FILE" "$MODULE_POM"

# ---------------------------------------------------------------------------
# Done
# ---------------------------------------------------------------------------

echo "SUCCESS: POM adapted for module '$MODULE_NAME' (type: $MODULE_TYPE)"
echo "  Parent:  uk.co.whitbread:digital-monorepo:\${revision}"
echo "  Version: \${revision}"
echo "  Path:    $MODULE_POM"
exit 0
