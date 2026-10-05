#!/usr/bin/env bash
# import-orchestrator.sh
#
# Bulk import orchestrator for migrating libraries and services into
# the digital-monorepo. Processes all libraries first, then all services,
# reading the ordered list from module-manifest.json.
#
# Usage:
#   ./scripts/import-orchestrator.sh [--resume-from <module-name>] [--dry-run]
#
# Flags:
#   --resume-from <module-name>  Skip modules already completed and resume
#                                from the specified module (reads import-state.json)
#   --dry-run                    Print what would be done without making changes
#
# Behaviour:
#   - Iterates libraries first, then services (from module-manifest.json)
#   - Calls sub-scripts for each module: pre-verify, adapt-pom, register-module,
#     wire-dependencies, verify-build
#   - Halts on any sub-script failure (non-zero exit)
#   - Tracks state in import-state.json after each successful module
#   - Prints a final summary report (success/failure/skipped counts)
#
# Exit codes:
#   0    All modules imported successfully
#   1    A module import failed (halted)
#   2    Invalid arguments or missing dependencies
#
# Requirements: 2.1, 2.2, 2.3, 2.5, 10.1

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
BACKEND_DIR="$REPO_ROOT/backend"
MANIFEST_FILE="$SCRIPT_DIR/module-manifest.json"
STATE_FILE="$SCRIPT_DIR/import-state.json"
MAVEN_SETTINGS="/Users/856124/Library/CloudStorage/OneDrive-Cognizant/Documents/Whitbread/maven/settings.xml"

# Sub-scripts
PRE_VERIFY="$SCRIPT_DIR/pre-verify.sh"
ADAPT_POM="$SCRIPT_DIR/adapt-pom.sh"
REGISTER_MODULE="$SCRIPT_DIR/register-module.sh"
WIRE_DEPS="$SCRIPT_DIR/wire-dependencies.sh"
VERIFY_BUILD="$SCRIPT_DIR/verify-build.sh"

# ---------------------------------------------------------------------------
# Argument Parsing
# ---------------------------------------------------------------------------

RESUME_FROM=""
DRY_RUN=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --resume-from)
      if [[ -z "${2:-}" ]]; then
        echo "ERROR: --resume-from requires a module name argument" >&2
        exit 2
      fi
      RESUME_FROM="$2"
      shift 2
      ;;
    --dry-run)
      DRY_RUN=true
      shift
      ;;
    -h|--help)
      sed -n '2,/^$/p' "$0" | sed 's/^# \?//'
      exit 0
      ;;
    *)
      echo "ERROR: Unknown argument: $1" >&2
      echo "Usage: $0 [--resume-from <module-name>] [--dry-run]" >&2
      exit 2
      ;;
  esac
done

# ---------------------------------------------------------------------------
# Dependency Checks
# ---------------------------------------------------------------------------

for cmd in jq; do
  if ! command -v "$cmd" &>/dev/null; then
    echo "ERROR: Required command '$cmd' not found in PATH" >&2
    exit 2
  fi
done

if [[ ! -f "$MANIFEST_FILE" ]]; then
  echo "ERROR: Module manifest not found: $MANIFEST_FILE" >&2
  exit 2
fi

# ---------------------------------------------------------------------------
# Helper Functions
# ---------------------------------------------------------------------------

log_info() {
  echo "[INFO] $(date '+%H:%M:%S') $*"
}

log_warn() {
  echo "[WARN] $(date '+%H:%M:%S') $*"
}

log_error() {
  echo "[ERROR] $(date '+%H:%M:%S') $*" >&2
}

log_dry() {
  echo "[DRY-RUN] $*"
}

# Check if a module name is in the completedModules array of import-state.json
is_module_completed() {
  local module_name="$1"
  if [[ ! -f "$STATE_FILE" ]]; then
    return 1
  fi
  jq -e --arg name "$module_name" \
    '.completedModules | map(.name) | index($name) != null' \
    "$STATE_FILE" &>/dev/null
}

# Update import-state.json with a successful module completion
record_success() {
  local module_name="$1"
  local module_type="$2"
  local duration_s="$3"

  local now
  now="$(date -u '+%Y-%m-%dT%H:%M:%SZ')"

  if [[ ! -f "$STATE_FILE" ]]; then
    # Create initial state file
    cat > "$STATE_FILE" <<EOF
{
  "startedAt": "$now",
  "completedModules": [],
  "failedModule": null,
  "nextModule": null
}
EOF
  fi

  # Update startedAt if null
  local started_at
  started_at="$(jq -r '.startedAt' "$STATE_FILE")"
  if [[ "$started_at" == "null" ]]; then
    jq --arg ts "$now" '.startedAt = $ts' "$STATE_FILE" > "${STATE_FILE}.tmp" \
      && mv "${STATE_FILE}.tmp" "$STATE_FILE"
  fi

  # Append to completedModules
  jq --arg name "$module_name" \
     --arg type "$module_type" \
     --arg status "success" \
     --argjson dur "$duration_s" \
     '.completedModules += [{"name": $name, "type": $type, "status": $status, "durationSeconds": $dur}]' \
     "$STATE_FILE" > "${STATE_FILE}.tmp" \
    && mv "${STATE_FILE}.tmp" "$STATE_FILE"
}

# Update import-state.json to record a failure and set nextModule
record_failure() {
  local module_name="$1"
  local module_type="$2"
  local exit_code="$3"

  local now
  now="$(date -u '+%Y-%m-%dT%H:%M:%SZ')"

  if [[ ! -f "$STATE_FILE" ]]; then
    cat > "$STATE_FILE" <<EOF
{
  "startedAt": "$now",
  "completedModules": [],
  "failedModule": null,
  "nextModule": null
}
EOF
  fi

  jq --arg name "$module_name" \
     --arg type "$module_type" \
     --argjson code "$exit_code" \
     --arg ts "$now" \
     '.failedModule = {"name": $name, "type": $type, "exitCode": $code, "failedAt": $ts} | .nextModule = $name' \
     "$STATE_FILE" > "${STATE_FILE}.tmp" \
    && mv "${STATE_FILE}.tmp" "$STATE_FILE"
}

# Update nextModule in import-state.json
update_next_module() {
  local next_name="$1"
  jq --arg name "$next_name" '.nextModule = $name' "$STATE_FILE" > "${STATE_FILE}.tmp" \
    && mv "${STATE_FILE}.tmp" "$STATE_FILE"
}

# Compute the module path within the backend directory
compute_module_path() {
  local module_type="$1"
  local squad="$2"
  local module_name="$3"

  if [[ "$module_type" == "library" ]]; then
    echo "${squad}/libs/${module_name}"
  else
    echo "${squad}/services/${module_name}"
  fi
}

# ---------------------------------------------------------------------------
# Read Module Manifest
# ---------------------------------------------------------------------------

# Build ordered arrays: libraries first, then services
LIBRARY_COUNT=$(jq '.libraries | length' "$MANIFEST_FILE")
SERVICE_COUNT=$(jq '.services | length' "$MANIFEST_FILE")
TOTAL_MODULES=$((LIBRARY_COUNT + SERVICE_COUNT))

log_info "Module manifest loaded: $LIBRARY_COUNT libraries, $SERVICE_COUNT services ($TOTAL_MODULES total)"

# ---------------------------------------------------------------------------
# Resume Logic
# ---------------------------------------------------------------------------

SKIP_UNTIL_FOUND=false
if [[ -n "$RESUME_FROM" ]]; then
  # Validate the module name exists in the manifest
  MODULE_EXISTS=$(jq --arg name "$RESUME_FROM" \
    '(.libraries + .services) | map(.name) | index($name) != null' "$MANIFEST_FILE")
  if [[ "$MODULE_EXISTS" != "true" ]]; then
    log_error "Module '$RESUME_FROM' not found in manifest"
    exit 2
  fi
  SKIP_UNTIL_FOUND=true
  log_info "Resume mode: will skip completed modules and resume from '$RESUME_FROM'"
fi

# ---------------------------------------------------------------------------
# Import Loop
# ---------------------------------------------------------------------------

SUCCESS_COUNT=0
FAILURE_COUNT=0
SKIPPED_COUNT=0
FAILED_MODULE_NAME=""

import_module() {
  local module_name="$1"
  local module_type="$2"  # "library" or "service"
  local squad="$3"
  local source_repo="$4"
  local group_id="${5:-}"
  local artifact_id="${6:-}"

  local module_path
  module_path="$(compute_module_path "$module_type" "$squad" "$module_name")"
  local full_module_dir="$BACKEND_DIR/$module_path"

  # --- Resume/skip logic ---
  if [[ "$SKIP_UNTIL_FOUND" == "true" ]]; then
    if is_module_completed "$module_name"; then
      log_info "SKIP (already completed): $module_name"
      ((SKIPPED_COUNT++)) || true
      return 0
    fi
    if [[ "$module_name" != "$RESUME_FROM" ]]; then
      log_info "SKIP (before resume point): $module_name"
      ((SKIPPED_COUNT++)) || true
      return 0
    fi
    # We've reached the resume point
    SKIP_UNTIL_FOUND=false
    log_info "Resuming from: $module_name"
  fi

  # --- Dry-run mode ---
  if [[ "$DRY_RUN" == "true" ]]; then
    log_dry "Would import $module_type '$module_name' (squad=$squad, repo=$source_repo) → $module_path"
    log_dry "  1. pre-verify.sh $full_module_dir/pom.xml $full_module_dir"
    log_dry "  2. adapt-pom.sh $full_module_dir/pom.xml $module_type"
    log_dry "  3. register-module.sh $module_type $squad $module_name ${group_id:-N/A} ${artifact_id:-N/A}"
    if [[ "$module_type" == "library" ]]; then
      log_dry "  4. wire-dependencies.sh $group_id $artifact_id"
    fi
    log_dry "  5. verify-build.sh $module_path"
    ((SUCCESS_COUNT++)) || true
    return 0
  fi

  log_info "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
  log_info "Importing $module_type: $module_name (squad=$squad)"
  log_info "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

  update_next_module "$module_name"
  local start_time
  start_time=$(date +%s)

  # --- Step 0: Clone source repository ---
  local clone_dir="/tmp/import-work/${module_name}"
  log_info "Step 0: Cloning source from https://github.com/${source_repo}.git"
  rm -rf "$clone_dir"
  mkdir -p "$clone_dir"
  if ! git clone --depth 1 "https://github.com/${source_repo}.git" "$clone_dir" 2>&1; then
    log_error "Failed to clone ${source_repo}"
    record_failure "$module_name" "$module_type" 4
    return 1
  fi

  # --- Step 1: Pre-verification ---
  local source_pom="$clone_dir/pom.xml"
  log_info "Step 1: Pre-verification"
  local rc=0
  "$PRE_VERIFY" "$source_pom" "$clone_dir" || rc=$?
  if (( rc != 0 )); then
    log_error "Pre-verification failed for $module_name (exit code: $rc)"
    record_failure "$module_name" "$module_type" "$rc"
    return 1
  fi

  # --- Step 1b: Place source in target directory ---
  log_info "Step 1b: Placing source in $module_path"
  mkdir -p "$full_module_dir"
  # Copy source, excluding standalone repo artifacts
  rsync -a --exclude='.git' --exclude='.github' --exclude='.mvn' \
    --exclude='mvnw' --exclude='mvnw.cmd' --exclude='.gitignore' \
    "$clone_dir/" "$full_module_dir/"

  # --- Step 2: POM Adaptation ---
  log_info "Step 2: Adapting POM"
  local module_pom="$full_module_dir/pom.xml"
  rc=0
  "$ADAPT_POM" "$module_pom" "$module_type" || rc=$?
  if (( rc != 0 )); then
    log_error "POM adaptation failed for $module_name (exit code: $rc)"
    record_failure "$module_name" "$module_type" "$rc"
    return 1
  fi

  # --- Step 3: Register module in root POM ---
  log_info "Step 3: Registering module"
  rc=0
  "$REGISTER_MODULE" "$module_type" "$squad" "$module_name" "${group_id:-}" "${artifact_id:-}" || rc=$?
  if (( rc != 0 )); then
    log_error "Module registration failed for $module_name (exit code: $rc)"
    record_failure "$module_name" "$module_type" "$rc"
    return 1
  fi

  # --- Step 4: Wire dependencies (libraries only) ---
  if [[ "$module_type" == "library" && -n "$group_id" && -n "$artifact_id" ]]; then
    log_info "Step 4: Wiring dependencies"
    rc=0
    "$WIRE_DEPS" "$group_id" "$artifact_id" || rc=$?
    if (( rc != 0 )); then
      log_error "Dependency wiring failed for $module_name (exit code: $rc)"
      record_failure "$module_name" "$module_type" "$rc"
      return 1
    fi
  fi

  # --- Step 5: Build verification ---
  log_info "Step 5: Build verification"
  rc=0
  "$VERIFY_BUILD" "$module_path" || rc=$?
  if (( rc != 0 )); then
    log_error "Build verification failed for $module_name (exit code: $rc)"
    record_failure "$module_name" "$module_type" "$rc"
    return 1
  fi

  # --- Success ---
  local end_time
  end_time=$(date +%s)
  local duration=$((end_time - start_time))

  record_success "$module_name" "$module_type" "$duration"
  log_info "✓ Successfully imported $module_name (${duration}s)"
  ((SUCCESS_COUNT++)) || true
  return 0
}

# ---------------------------------------------------------------------------
# Process Libraries (first)
# ---------------------------------------------------------------------------

log_info "═══════════════════════════════════════════════════════"
log_info "Phase 1: Importing Libraries ($LIBRARY_COUNT modules)"
log_info "═══════════════════════════════════════════════════════"

for i in $(seq 0 $((LIBRARY_COUNT - 1))); do
  name=$(jq -r ".libraries[$i].name" "$MANIFEST_FILE")
  squad=$(jq -r ".libraries[$i].squad" "$MANIFEST_FILE")
  group_id=$(jq -r ".libraries[$i].groupId" "$MANIFEST_FILE")
  artifact_id=$(jq -r ".libraries[$i].artifactId" "$MANIFEST_FILE")
  source_repo=$(jq -r ".libraries[$i].sourceRepo" "$MANIFEST_FILE")

  if ! import_module "$name" "library" "$squad" "$source_repo" "$group_id" "$artifact_id"; then
    FAILURE_COUNT=1
    FAILED_MODULE_NAME="$name"
    break
  fi
done

# ---------------------------------------------------------------------------
# Process Services (second, only if no library failures)
# ---------------------------------------------------------------------------

if (( FAILURE_COUNT == 0 )); then
  log_info ""
  log_info "═══════════════════════════════════════════════════════"
  log_info "Phase 2: Importing Services ($SERVICE_COUNT modules)"
  log_info "═══════════════════════════════════════════════════════"

  for i in $(seq 0 $((SERVICE_COUNT - 1))); do
    name=$(jq -r ".services[$i].name" "$MANIFEST_FILE")
    squad=$(jq -r ".services[$i].squad" "$MANIFEST_FILE")
    source_repo=$(jq -r ".services[$i].sourceRepo" "$MANIFEST_FILE")
    # Services don't have groupId/artifactId in manifest (they're not registered in dependencyManagement)
    group_id=$(jq -r ".services[$i].groupId // empty" "$MANIFEST_FILE")
    artifact_id=$(jq -r ".services[$i].artifactId // empty" "$MANIFEST_FILE")

    if ! import_module "$name" "service" "$squad" "$source_repo" "$group_id" "$artifact_id"; then
      FAILURE_COUNT=1
      FAILED_MODULE_NAME="$name"
      break
    fi
  done
fi

# ---------------------------------------------------------------------------
# Final Summary Report
# ---------------------------------------------------------------------------

echo ""
echo "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓"
echo "┃           IMPORT SUMMARY REPORT                      ┃"
echo "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫"
printf "┃  Total modules:    %-33s┃\n" "$TOTAL_MODULES"
printf "┃  Successful:       %-33s┃\n" "$SUCCESS_COUNT"
printf "┃  Failed:           %-33s┃\n" "$FAILURE_COUNT"
printf "┃  Skipped:          %-33s┃\n" "$SKIPPED_COUNT"
echo "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┫"

if (( FAILURE_COUNT > 0 )); then
  printf "┃  Status:           %-33s┃\n" "FAILED"
  printf "┃  Failed module:    %-33s┃\n" "$FAILED_MODULE_NAME"
  echo "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛"
  echo ""
  log_error "Import halted due to failure in module: $FAILED_MODULE_NAME"
  log_info "To resume after fixing, run:"
  log_info "  $0 --resume-from $FAILED_MODULE_NAME"
  exit 1
else
  printf "┃  Status:           %-33s┃\n" "SUCCESS"
  echo "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛"
  echo ""
  log_info "All $TOTAL_MODULES modules imported successfully!"
  exit 0
fi
