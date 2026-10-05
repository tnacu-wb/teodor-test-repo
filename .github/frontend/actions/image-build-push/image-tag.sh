#!/usr/bin/env bash

generate_frontend_image_tag() {
  if [[ $# -lt 3 || $# -gt 5 ]]; then
    printf 'Usage: generate_frontend_image_tag <app-version> <ref-name> <commit-sha> [timestamp] [semantic-version]\n' >&2
    return 1
  fi

  local app_version=$1
  local ref_name=$2
  local commit_sha=$3
  local timestamp=${4:-$(date +%s)}
  local semantic_version=${5:-}
  local tag_ref

  if [[ ! "$commit_sha" =~ ^[[:xdigit:]]{40}$ ]]; then
    printf 'Commit SHA must contain exactly 40 hexadecimal characters\n' >&2
    return 1
  fi

  if [[ ! "$timestamp" =~ ^[0-9]{10}$ ]]; then
    printf 'Timestamp must contain exactly 10 digits\n' >&2
    return 1
  fi

  if [[ -n "$semantic_version" ]]; then
    if [[ "$ref_name" != "main" ]]; then
      printf 'Semantic version override is reserved for production main builds\n' >&2
      return 1
    fi
    if [[ ! "$semantic_version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      printf 'Semantic version must use X.Y.Z format\n' >&2
      return 1
    fi
    printf '%s\n' "$semantic_version"
    return 0
  fi

  case "$ref_name" in
    main)
      tag_ref=develop
      ;;
    *gh-readonly-queue*)
      tag_ref=main
      ;;
    *)
      tag_ref=${ref_name//\//-}
      ;;
  esac

  printf '%s-%s-%s-%s\n' "$app_version" "$tag_ref" "$commit_sha" "$timestamp"
}
