#!/bin/sh
set -e

echo "Firebase plist copy script running for configuration: ${CONFIGURATION}"

DEBUG_PLIST="${PROJECT_DIR}/PremierInn/Resources/Targets/PremierInn-DEV/GoogleService-Info-Debug.plist"
DEST_PLIST="${BUILT_PRODUCTS_DIR}/${WRAPPER_NAME}/GoogleService-Info.plist"

if [ ! -f "${DEBUG_PLIST}" ]; then
  echo "❌ DEBUG GoogleService-Info-Debug.plist not found at: ${DEBUG_PLIST}"
  exit 1
fi

cp -f "${DEBUG_PLIST}" "${DEST_PLIST}"

echo "✅ Copied Debug GoogleService-Info.plist → ${DEST_PLIST}"
