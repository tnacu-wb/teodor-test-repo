#!/usr/bin/env python3
#
# Check GitHub Security Advisories for a single repo and post new/updated ones to Teams.
#
# Called from .github/workflows/github-security-advisory-notifier.yaml (one repo per
# matrix leg). Dedup state is persisted to seen-advisories.json, cached across runs
# by the workflow.
#
# Environment:
#   REPO           Matrix repo slug, e.g. "kedacore/keda". Required.
#   GH_TOKEN       GitHub App installation token. Optional — only raises the API
#                  rate limit; falls back to unauthenticated if unset or rejected.
#   TEAMS_WEBHOOK  Teams incoming webhook URL. Required.

import json
import os
import sys
import time
from pathlib import Path

import requests

REPO = os.environ["REPO"]
STATE_FILE = Path("seen-advisories.json")

# Teams incoming webhooks throttle above ~4 requests/second, returning 429 (and
# sometimes 403). See "Rate limiting for connectors" in the Microsoft docs:
# https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook
# That matters here because a run with an empty dedup cache posts the whole
# backlog in one go (~35 advisories across the four repos on the first run).
# Pace the posts well under the documented ceiling and retry with exponential
# backoff, as the docs recommend, so a burst doesn't fail the run.
POST_INTERVAL_SECONDS = 0.5
MAX_POST_ATTEMPTS = 5
RETRY_STATUS_CODES = {403, 429, 500, 502, 503, 504}

gh_token = os.environ.get("GH_TOKEN", "")
teams_webhook = os.environ.get("TEAMS_WEBHOOK", "")

if not teams_webhook:
    print("::error::SECURITY_ADVISORY_TEAMS_WEBHOOK secret is not set")
    sys.exit(1)

def fetch_advisories(token):
    """Fetch every published advisory for REPO, following pagination.

    The security-advisories endpoint is paginated (default 30 per page), so
    follow the rel="next" Link header until exhausted — otherwise older
    advisories never get seen or deduped, and items shifting between pages
    cause missed or repeated notifications.
    """
    headers = {
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"

    collected = []
    url = f"https://api.github.com/repos/{REPO}/security-advisories"
    params = {"per_page": 100}
    while url:
        resp = requests.get(url, headers=headers, params=params, timeout=30)
        resp.raise_for_status()
        collected.extend(resp.json())
        # params only needs to apply to the first request; subsequent page URLs
        # from the Link header already carry the query string.
        params = None
        url = resp.links.get("next", {}).get("url")
    return collected


def is_rate_limited(resp):
    """True when a 403/429 is GitHub rate limiting rather than authorization."""
    if resp is None:
        return False
    return resp.headers.get("X-RateLimit-Remaining") == "0"


def set_output(name, value):
    """Write a step output for the workflow, if running under Actions."""
    output_path = os.environ.get("GITHUB_OUTPUT")
    if output_path:
        with open(output_path, "a", encoding="utf-8") as fh:
            fh.write(f"{name}={value}\n")


def save_state(state):
    """Persist dedup state atomically and signal that it changed.

    Writing straight to STATE_FILE truncates it in place, so an interruption
    mid-write could leave a partial file behind — and the workflow's cache save
    step runs on cancellation as well as failure, so that partial file would get
    cached and then fail to parse on every later run. Write to a sibling temp
    file and rename instead; os.replace is atomic within a filesystem.

    Every write is also the only thing worth caching, so emit state_changed=true
    here. The cache key is unique per run, so without this gate the workflow
    would save a fresh (identical) cache entry on every no-change run — needless
    churn on a 6-hourly job. Runs that never call save_state leave the output
    unset (falsey) and skip the save.
    """
    tmp_file = STATE_FILE.with_suffix(".json.tmp")
    tmp_file.write_text(json.dumps(state, indent=2, sort_keys=True))
    os.replace(tmp_file, STATE_FILE)
    set_output("state_changed", "true")


# The advisories we read belong to public upstream repos (kedacore/keda etc.),
# so the endpoint works unauthenticated — the token only raises the rate limit
# from 60 req/hour (shared across the runner IP) to 15,000. The token comes from
# the pipeline GitHub App, which is installed on our org and not on those
# upstream repos. Installation tokens do normally read public data outside their
# installation, but rather than depend on that, treat an auth failure as a
# signal to retry unauthenticated: a wrong assumption then costs a log warning
# instead of a failed run, and the first run tells us definitively. Genuine rate
# limiting is excluded so it still surfaces as an error rather than silently
# downgrading to the much lower unauthenticated ceiling.
try:
    advisories = fetch_advisories(gh_token)
except requests.HTTPError as exc:
    status = exc.response.status_code if exc.response is not None else None
    if gh_token and status in (401, 403, 404) and not is_rate_limited(exc.response):
        print(
            f"::warning::GitHub API returned {status} for {REPO} with the app "
            "installation token; retrying unauthenticated"
        )
        advisories = fetch_advisories("")
    else:
        raise

# Dedup state keyed by ghsa_id -> revision marker. The marker is normally
# updated_at, so we re-notify when an existing advisory is revised (e.g. CVSS
# score change, new patched version) rather than only ever firing once per
# ghsa_id. updated_at can be null in the API, though; falling back to
# published_at then ghsa_id guarantees a non-null marker. Without that
# fallback, a null updated_at would compare equal to a missing state entry
# (None == None), so the advisory would be treated as already-seen and never
# notified. The compare and the persist below MUST use this same value.
def revision_marker(advisory):
    return advisory.get("updated_at") or advisory.get("published_at") or advisory["ghsa_id"]

seen = {}
restored_state_invalid = False
if STATE_FILE.exists():
    # Restored state comes from a cache entry, so treat it as untrusted. Failing
    # hard would wedge every future run until the cache expired; starting from
    # empty instead re-notifies the backlog once and self-heals on the next
    # save. Guard both failure modes: JSON that won't parse, and JSON that
    # parses to the wrong shape (e.g. a list), which would make the seen.get(...)
    # lookups below raise.
    try:
        loaded = json.loads(STATE_FILE.read_text())
    except (json.JSONDecodeError, UnicodeDecodeError):
        # read_text() decodes before json.loads runs, so a cache entry that
        # isn't valid UTF-8 raises UnicodeDecodeError, not JSONDecodeError.
        # Both mean the restored state is unusable; recover the same way.
        loaded = None
    if isinstance(loaded, dict):
        seen = loaded
    else:
        print(f"::warning::Dedup state for {REPO} is unreadable; starting from empty state")
        seen = {}
        # The corrupt file still exists on disk. Remember that so the no-advisory
        # exit path below rewrites a clean {} even though the file "exists" —
        # otherwise the bad cache would be re-saved and re-warn on every run.
        restored_state_invalid = True

new_or_updated = [
    a for a in advisories
    if a.get("state") == "published"
    and seen.get(a["ghsa_id"]) != revision_marker(a)
]

if not new_or_updated:
    print(f"::notice::No new or updated advisories for {REPO}")
    # Write the state file when nothing changed only if it's missing (a first
    # run or a zero-advisory repo, so the cache/save step has a path) or if what
    # we restored was corrupt (rewrite a clean {} so the bad cache self-heals
    # instead of re-warning every run). A valid no-op run writes nothing, so
    # save_state doesn't signal state_changed and the cache save is skipped.
    if not STATE_FILE.exists() or restored_state_invalid:
        save_state(seen)
    sys.exit(0)

def post_to_teams(card, ghsa_id):
    """POST one Adaptive Card, retrying throttled and transient failures.

    Honours Retry-After when Teams supplies it, otherwise backs off
    exponentially (1s, 2s, 4s, 8s). Raises on a non-retryable status or once
    attempts are exhausted, so a genuine failure still fails the step.
    """
    for attempt in range(1, MAX_POST_ATTEMPTS + 1):
        retry_after = None
        try:
            resp = requests.post(teams_webhook, json=card, timeout=30)
        except requests.RequestException:
            # A transport-level failure (connection reset, timeout, DNS) is worth
            # retrying like a 5xx. Crucially, do NOT let this exception reach the
            # traceback: requests/urllib3 can embed the full request URL — here
            # the webhook secret — in its message. Raise from None on the last
            # attempt so neither the message nor the chained cause leaks it.
            reason = "connection error"
            status = None
        else:
            status = resp.status_code
            if status not in RETRY_STATUS_CODES:
                # Don't use resp.raise_for_status() either: it formats the URL
                # into the message the same way.
                if status >= 400:
                    raise RuntimeError(
                        f"Teams webhook POST for {ghsa_id} failed with {status}"
                    )
                return
            reason = f"status {status}"
            retry_after = resp.headers.get("Retry-After")

        if attempt == MAX_POST_ATTEMPTS:
            raise RuntimeError(
                f"Teams webhook POST for {ghsa_id} failed ({reason}) "
                f"after {MAX_POST_ATTEMPTS} attempts"
            ) from None

        try:
            delay = float(retry_after) if retry_after is not None else 2 ** (attempt - 1)
        except ValueError:
            # Retry-After may be an HTTP-date rather than seconds; fall back.
            delay = 2 ** (attempt - 1)

        print(
            f"::warning::Teams webhook POST for {ghsa_id} failed ({reason}), "
            f"retrying in {delay:g}s (attempt {attempt}/{MAX_POST_ATTEMPTS})"
        )
        time.sleep(delay)

count = len(new_or_updated)
noun = "advisory" if count == 1 else "advisories"
print(f"::notice::{count} new/updated {noun} for {REPO}")

for advisory in new_or_updated:
    severity = (advisory.get("severity") or "unknown").upper()
    cve_id = advisory.get("cve_id") or "N/A"
    # NOTE: on the *repository* advisories endpoint (GET /repos/{repo}/security-
    # advisories, what this script calls) each vulnerability's patched version is
    # a plain string field named `patched_versions`, e.g. "1.29.2, 1.28.6". This
    # is NOT the global advisories endpoint (GET /advisories), which nests it as
    # first_patched_version.identifier — reading that here would yield "unknown"
    # for every advisory. Verified against the live API for istio/istio and
    # apollographql/router. Keep the field name as-is.
    patched = ", ".join(
        v.get("patched_versions") or "unknown"
        for v in advisory.get("vulnerabilities", [])
    ) or "unknown"

    card = {
        "type": "message",
        "attachments": [
            {
                "contentType": "application/vnd.microsoft.card.adaptive",
                "content": {
                    "$schema": "https://adaptivecards.io/schemas/adaptive-card.json",
                    "type": "AdaptiveCard",
                    "version": "1.5",
                    "msteams": {"width": "Full"},
                    "body": [
                        {
                            "type": "TextBlock",
                            "text": f"🚨 Security Advisory: {REPO}",
                            "weight": "Bolder",
                            "size": "Large",
                            "wrap": True,
                        },
                        {
                            "type": "TextBlock",
                            "text": advisory.get("summary", "(no summary)"),
                            "weight": "Bolder",
                            "size": "Medium",
                            "wrap": True,
                            "spacing": "Small",
                        },
                        {
                            "type": "FactSet",
                            "separator": True,
                            "facts": [
                                {"title": "Severity:", "value": severity},
                                {"title": "CVE:", "value": cve_id},
                                {"title": "GHSA:", "value": advisory["ghsa_id"]},
                                {"title": "Patched versions:", "value": patched},
                                {"title": "Published:", "value": advisory.get("published_at") or "unknown"},
                            ],
                        },
                    ],
                    "actions": [
                        {
                            "type": "Action.OpenUrl",
                            "title": "View advisory on GitHub",
                            "url": advisory["html_url"],
                        }
                    ],
                },
            }
        ],
    }

    post_to_teams(card, advisory["ghsa_id"])
    print(f"::notice::Posted {advisory['ghsa_id']} ({severity}) for {REPO} to Teams ✅")

    # Persist after each successful post rather than once at the end. If a later
    # advisory fails (transient Teams error), the ones already delivered stay
    # recorded, so the next run doesn't re-post them. The workflow saves the
    # cache even when this step fails, so partial progress isn't lost.
    seen[advisory["ghsa_id"]] = revision_marker(advisory)
    save_state(seen)

    # Pace posts to stay clear of the webhook's ~4/second ceiling.
    if advisory is not new_or_updated[-1]:
        time.sleep(POST_INTERVAL_SECONDS)
