#!/bin/bash

# Script to patch Adobe SDK SQLiteWrapper to enable WAL mode
# This script automatically applies the WAL mode fix to the SPM DerivedData cache to solve the violation
# Run this as a build phase in Xcode

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# Find SQLiteWrapper.swift in SPM DerivedData cache
DERIVED_DATA_BASE="${HOME}/Library/Developer/Xcode/DerivedData"
PROJECT_DERIVED_DATA=$(find "$DERIVED_DATA_BASE" -maxdepth 1 -name "PremierInn-*" -type d 2>/dev/null | head -1)

if [ -z "$PROJECT_DERIVED_DATA" ]; then
    echo "⚠️ Project DerivedData directory not found. SDK might not be downloaded yet."
    exit 0
fi

SQLITE_WRAPPER=$(find "$PROJECT_DERIVED_DATA/SourcePackages/checkouts" -name "SQLiteWrapper.swift" -path "*/AEPServices/*" 2>/dev/null | head -1)

if [ -z "$SQLITE_WRAPPER" ]; then
    echo "⚠️ SQLiteWrapper.swift not found in SPM DerivedData cache"
    exit 0
fi

# Check if already patched
if grep -q "PRAGMA journal_mode=WAL" "$SQLITE_WRAPPER"; then
    echo "✅ SQLiteWrapper already patched with WAL mode"
    exit 0
fi

# Apply the patch
echo "🔧 Patching SQLiteWrapper.swift to enable WAL mode at: $SQLITE_WRAPPER"

# Create backup
cp "$SQLITE_WRAPPER" "${SQLITE_WRAPPER}.backup"

# Apply the fix using sed
sed -i '' 's/return database$/\/\/ Enable WAL mode to reduce I\/O and improve performance\n            if let db = database {\n                sqlite3_exec(db, "PRAGMA journal_mode=WAL;", nil, nil, nil)\n            }\n            return database/' "$SQLITE_WRAPPER"

echo "✅ Successfully patched SQLiteWrapper.swift with WAL mode"
