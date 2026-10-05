SWIFT_PACKAGE_DIR="${SWIFT_PACKAGE_DIR:-${BUILD_DIR%Build/*}SourcePackages}"
SWIFTLINT_CMD="$SWIFT_PACKAGE_DIR/artifacts/swiftlintplugins/SwiftLintBinary/SwiftLintBinary.artifactbundle/macos/swiftlint"

if test -f "$SWIFTLINT_CMD"; then
    cd "$SRCROOT"
    "$SWIFTLINT_CMD" lint \
        --config "$SRCROOT/.swiftlint.yml" \
        "$SRCROOT/PremierInn" \
        "$SRCROOT/SimpleNetwork/Sources" \
        "$SRCROOT/TakeMeBack"
else
    echo "warning: swiftlint command not found - See https://github.com/realm/SwiftLint#xcode-run-script-build-phase for installation instructions."
fi
