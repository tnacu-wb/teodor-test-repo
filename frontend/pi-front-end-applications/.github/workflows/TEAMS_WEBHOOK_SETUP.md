# Microsoft Teams Trigger for GitHub Actions

This document explains how to trigger the `teams-trigger` workflow when someone raises a change request in a Microsoft Teams channel.

The workflow is a thin dispatcher: it takes three fields — `changes_requested`, `attachements` and `message_id` — and hands them to the reusable
`whitbread-eos/wbd-workflows-templates/.github/workflows/ci-teams-issue-resolver.yaml@test-teams-kiro-integration`, which runs the Kiro CLI,
opens a PR on a `<prefix>/CTECH-<message_id>-TEAMS-XX` branch, and replies in the Teams thread — first "In Progress", then the steps to
replicate once the ephemeral environment is deployed.

## Overview

Teams cannot call a GitHub workflow directly — GitHub's API needs a specific `Authorization` header and JSON body that Teams itself won't produce. So the chain is always:

```
Someone posts in a Teams channel
        ↓
Teams-side sender (Power Automate flow, or an outgoing webhook + relay)
        ↓  POST https://api.github.com/repos/<org>/<repo>/dispatches
        ↓  { "event_type": "teams-message", "client_payload": { ... } }
GitHub repository_dispatch
        ↓
.github/workflows/teams-trigger.yaml
```

`teams-trigger.yaml` accepts two triggers:

1. **`repository_dispatch`** with `event_type: teams-message` — the real path, from Teams.
2. **`workflow_dispatch`** — manual run from the Actions UI, for testing without Teams.

It can also reply in the Teams thread with its progress ("in progress" on start, then the outcome). That's a separate, optional leg — see [Step 2](#step-2--status-replies-in-the-teams-thread-optional).

Pick one of the two Teams-side options below. Option A needs no hosting but needs a premium licence; Option B needs no licence but you host a small relay.

---

## Step 1 — Create a GitHub token (both options)

The Teams side needs a token to call the dispatch API.

**Fine-grained token (preferred):**

1. GitHub → Settings → Developer settings → Personal access tokens → Fine-grained tokens → Generate new token
2. Resource owner: `whitbread-eos`, repository access: only `pi-front-end-applications`
3. Repository permissions: **Contents: Read and write** (this is what `repository_dispatch` requires)
4. Set the shortest expiry you can live with, generate, and **copy the token**

**Classic token (alternative):** scopes `repo` and `workflow`.

Store it wherever the sender can read it as a secret — never in the flow body as plain text:

- Power Automate: an [Azure Key Vault](https://learn.microsoft.com/en-us/azure/key-vault/) reference, or a Power Platform environment variable
- Relay: an app setting / Key Vault reference on the Function

> A token that can `repository_dispatch` can also push to the repo. Treat it accordingly, and rotate it on the same cadence as your other CI credentials.

---

## Option A — Power Automate flow (recommended)

No code to host. Note the **HTTP** action is a premium connector, so this needs a Power Automate Premium licence. If you don't have one, use Option B.

### A1. Create the flow

1. Go to [make.powerautomate.com](https://make.powerautomate.com) → Create → **Automated cloud flow**
2. Trigger: search Teams → **When a new channel message is added**
3. Set **Team** and **Channel** to the channel you want to watch

The trigger polls (roughly every minute on most licences) rather than firing instantly — expect a short delay before the workflow starts.

### A2. Convert the message to plain text first

The Teams trigger returns the message body as **HTML** (`<div>/ci develop</div>`), not plain text. Do this before the condition — otherwise every string comparison is fighting the markup, and `startsWith(..., '/ci')` never matches because the value starts with `<`.

Add a **Html to text** action (built-in **Content Conversion** connector — not premium):

- **Content:** the trigger's *Message body content* field, from the dynamic content picker

Later steps reference it as `body('Html_to_text')` — in expressions, action names use underscores instead of spaces.

> If your connector version already exposes a plain-text body field in the dynamic content picker, use that and skip this action.

### A3. Filter which messages trigger a run (recommended)

Without a filter, *every* message in the channel starts a workflow run. Add a **Condition** after the Html to text action, and put the HTTP action in the **If yes** branch.

In the Condition card, click **Edit in advanced mode** and paste one expression. Pick whichever matches how you want the channel to work:

**Only messages starting with a command word** — the usual choice:

```
@startsWith(trim(body('Html_to_text')), '/ci')
```

**A keyword anywhere in the message**, case-insensitive:

```
@contains(toLower(body('Html_to_text')), 'deploy')
```

**Only certain people can trigger runs** — worth adding, since otherwise anyone who can post in the channel can start a pipeline:

```
@contains(createArray('iyyappan.murugan@whitbread.com','someone.else@whitbread.com'), triggerOutputs()?['body/from/user/email'])
```

**Command prefix _and_ an allowed sender** — combine with `and()`:

```
@and(
  startsWith(trim(body('Html_to_text')), '/ci'),
  contains(createArray('iyyappan.murugan@whitbread.com'), triggerOutputs()?['body/from/user/email'])
)
```

**Ignore posts from bots and other flows** (a human posted it):

```
@empty(coalesce(triggerOutputs()?['body/from/application'], ''))
```

**Ignore threaded replies**, so only top-level posts count:

```
@equals(triggerOutputs()?['body/replyToId'], null)
```

`trim()` matters in the prefix checks — Teams often leaves a trailing newline or `&nbsp;`, and a leading space would break a bare `startsWith`.

> **Cheaper alternative:** put the same expression in the trigger's own **Settings → Trigger Conditions** instead of a Condition action. The flow then doesn't run at all for non-matching messages, so it doesn't consume a run from your quota — useful on a busy channel. The expression syntax is identical, but it can only reference `triggerOutputs()`, not `body('Html_to_text')`, so for a prefix check there you have to match against the raw HTML (`@contains(triggerOutputs()?['body/body/content'], '/ci')`).

### A4. Add the HTTP action

First add a **Compose** action named `Sanitise`, so a message containing a quote character can't break the JSON body:

```
@{replace(replace(replace(body('Html_to_text'), '\', '\\'), '"', '\"'), decodeUriComponent('%0A'), ' ')}
```

Then add an **HTTP** action:

- **Method:** `POST`
- **URI:** `https://api.github.com/repos/whitbread-eos/pi-front-end-applications/dispatches`
- **Headers:**

  | Key | Value |
  | --- | --- |
  | `Authorization` | `Bearer <your token>` |
  | `Accept` | `application/vnd.github+json` |
  | `Content-Type` | `application/json` |
  | `User-Agent` | `power-automate-teams-trigger` |

- **Body:**

```json
{
  "event_type": "teams-message",
  "client_payload": {
    "changes_requested": "@{outputs('Sanitise')}",
    "attachements": @{triggerOutputs()?['body/attachments']},
    "message_id": "@{triggerOutputs()?['body/id']}"
  }
}
```

`attachements` is sent unquoted so it stays a JSON **array** (send `[]` when there are none). `changes_requested` and `message_id` are strings.

Build these values from the **dynamic content picker** rather than typing the paths by hand — the exact field names vary between connector versions, and the picker always shows what your version actually emits.

Action names in expressions must match what the designer actually called them — Power Automate renames duplicates to `Compose 2`, `Html to text 2` and so on, and the expression has to use the same name with underscores.

### A5. Avoid trigger loops

This matters as soon as you enable the status replies in Step 2: the workflow posts back into the *same channel* the trigger watches, so those replies can re-fire the trigger — pipeline posts "in progress", trigger fires, pipeline posts again, and so on.

The command-prefix condition from A3 already prevents it, which is the main reason to prefer it over a bare "any message" trigger: `⏳ Pipeline in progress — …` doesn't start with `/ci`, so it gets filtered out. If you drop the prefix filter, add one of these instead:

```
@empty(coalesce(triggerOutputs()?['body/from/application'], ''))
```

That skips anything posted by a flow or bot rather than a person, which is what the status replies are. Confirm it works by watching the trigger flow's run history after a pipeline finishes — you should see the reply either not trigger a run at all, or trigger one that stops at the condition.

### A6. Test it

1. Save the flow, post `/ci hello` in the channel
2. Power Automate → the flow → **28-day run history** — confirm the HTTP action returned **204 No Content**
3. GitHub → Actions → **Kiro Teams Change Request Resolver** — confirm a run appeared with your change request in the job summary

---

## Option B — Teams outgoing webhook + relay

No premium licence, but two real constraints:

- An outgoing webhook only fires when the webhook is **@mentioned**, not on every message.
- Teams requires an HTTPS endpoint that verifies an HMAC signature and **replies within 5 seconds** with a message payload. GitHub's API does neither, so a small relay of your own sits in between.

### B1. Create the outgoing webhook

1. In Teams, go to the team → **⋯** → **Manage team** → **Apps** → **Create an outgoing webhook**
2. Name it (e.g. `github-ci`), set the callback URL to your relay's HTTPS endpoint, add a description
3. Teams shows a **security token** once — copy it; the relay needs it to verify signatures

### B2. What the relay must do

Host it wherever you already run small services (Azure Function, Lambda, Container App). Its contract:

1. **Verify the signature.** Teams sends `Authorization: HMAC <base64-signature>`, where the signature is HMAC-SHA256 over the **raw request body bytes**, keyed by the base64-decoded security token. Compare with a constant-time comparison and reject mismatches with `401`. Verify before parsing — re-serialising the JSON changes the bytes and breaks the check.
2. **Read the message.** The Teams activity payload has the text in `text` (with the `<at>github-ci</at>` mention markup included — strip it), the poster in `from.name`, and the conversation in `conversation.id`.
3. **Call GitHub.** `POST https://api.github.com/repos/whitbread-eos/pi-front-end-applications/dispatches` with the same headers and `client_payload` shape as Option A above.
4. **Reply to Teams within 5 seconds** with `{"type": "message", "text": "Pipeline triggered ✅"}` so the user sees an acknowledgement in the channel. If the GitHub call might be slow, reply first and fire the dispatch asynchronously.

Keep the GitHub token in the relay's secret store, never in its source.

### B3. Test it

@mention the webhook in the channel (`@github-ci /ci hello`) and check both the relay logs and the Actions run list.

---

## Step 2 — Status replies in the Teams thread (optional)

`teams-trigger.yaml` posts its own progress updates back into the thread the message came from: an "in progress" reply as soon as the run starts, and a success/failure/cancelled reply when it ends. Both come from GitHub Actions, not from the flow that triggered it, so they reflect what the pipeline actually did.

This needs a **second** Power Automate flow — a callback the workflow can POST to.

### S1. Create the callback flow

1. make.powerautomate.com → Create → **Instant cloud flow** → trigger **When an HTTP request is received**
2. Paste this into **Request Body JSON Schema**:

```json
{
  "type": "object",
  "properties": {
    "message_id": { "type": "string" },
    "team": { "type": "string" },
    "channel": { "type": "string" },
    "status": { "type": "string" },
    "text": { "type": "string" },
    "run_url": { "type": "string" }
  }
}
```

3. Add the Teams action **Reply with a message in a channel**:
   - **Team:** `team` from the request body (or hard-code it)
   - **Channel:** `channel` from the request body (or hard-code it)
   - **Message ID:** `message_id`
   - **Message:** `text`
4. **Save**, then copy the generated **HTTP POST URL** from the trigger card — it only appears after the first save

The workflow sends both a human-ready `text` and a machine-readable `status` (`in_progress`, `success`, `failure`, `cancelled`). Post `text` as-is for the simple version, or switch on `status` if you want different formatting or an Adaptive Card per outcome.

### S2. Store the URL in GitHub

That URL contains its own SAS signature — anyone holding it can post to your channel, so treat it as a credential:

Repo → Settings → Secrets and variables → Actions → New repository secret

- **Name:** `TEAMS_CALLBACK_URL`
- **Value:** the HTTP POST URL from S1

**If the secret is absent, both reply steps skip and the run continues** — the pipeline works fine without it, you just get no thread updates. Same when `teams_message_id` is missing from the payload (there'd be nothing to reply to), which is the normal case for a manual `workflow_dispatch` run.

The reply steps also never fail the run: a Teams or Power Automate outage logs a warning and the pipeline carries on.

### S3. Make sure the trigger flow sends `message_id`

The reply has to know which message to thread under, and the branch name is built from it. The `client_payload` in A4 already includes it — check it's mapped, or the run fails and no reply is posted.

### S4. Test it

```bash
curl -sS -X POST "$TEAMS_CALLBACK_URL" \
  -H 'Content-Type: application/json' \
  -d '{
    "message_id": "<id of a real message>",
    "team": "<team id>",
    "channel": "<channel id>",
    "status": "in_progress",
    "text": "Test reply from curl"
  }'
```

A threaded reply should appear under that message within a second or two. Get a real `message_id` from the trigger flow's run history, or from `linkToMessage` — it's the long number at the end of the URL.

---

## Payload reference

`teams-trigger.yaml` reads exactly three fields from `github.event.client_payload` and passes them to the reusable resolver:

| Field | Type | Required | Passed to the resolver as |
| --- | --- | --- | --- |
| `changes_requested` | string | **yes** — the run fails without it | `changes_requested` |
| `attachements` | JSON array | no (defaults to `[]`) | `attachements` (normalised to a JSON string) |
| `message_id` | string | **yes** — drives the branch name and the thread reply | `message_id` |

An example payload as sent by the flow:

```json
{
  "changes_requested": "Currently the cookie consent banner is looking like ... We want to experiment with other modals ...",
  "attachements": [],
  "message_id": "1787582381329"
}
```

The resolver derives everything else itself:

| Value | Where it comes from |
| --- | --- |
| Branch name | `<feat\|fix>/CTECH-<message_id>-TEAMS-XX` |
| Teams team | hardcoded `Digital Engineering` |
| Teams channel | hardcoded `Kiro-Test` |
| `run_url` in replies | the current Actions run URL |

`-TEAMS-XX` is the differentiator that marks a PR as raised from Teams, mirroring `-Issues-XX` (GitHub issues) and `-JIRA-XX` (Jira). `ci-pipeline-fe.yaml` keys the `post-teams-comment` job off it, and `ci-fe-build.yaml` / `ci-pipeline-fe.yaml` skip the same scans they skip for the other two.

Add your own fields by extending the `client_payload` on the Teams side, the `with:` block in `teams-trigger.yaml`, and the `workflow_call` inputs of `ci-teams-issue-resolver.yaml`.

---

## Testing without Teams

**Simulate the webhook with curl** — the fastest way to confirm the GitHub side works before touching Teams:

```bash
curl -X POST \
  -H "Authorization: Bearer $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  https://api.github.com/repos/whitbread-eos/pi-front-end-applications/dispatches \
  -d '{
    "event_type": "teams-message",
    "client_payload": {
      "changes_requested": "Move the cookie consent banner to the bottom right when the query param cookieBanner=bottomright is present.",
      "attachements": [],
      "message_id": "1787582381329"
    }
  }'
```

A successful call returns **204 No Content** and prints nothing. Then check Actions → Kiro Teams Change Request Resolver.

**Or run it manually:** Actions → Kiro Teams Change Request Resolver → **Run workflow**, and fill in `changes_requested` and `message_id` (leave `attachements` as `[]`).

> `repository_dispatch` only fires for workflows on the **default branch** (`develop`). While developing on a feature branch, test with `workflow_dispatch`; the dispatch path starts working once the workflow is merged.

---

## Troubleshooting

| Symptom | Cause / fix |
| --- | --- |
| `401 Unauthorized` | Token invalid, expired, or missing `Contents: write` / `repo`. |
| `404 Not Found` | Wrong `owner/repo` in the URL, or the token can't see a private repo. 404 is also what GitHub returns instead of 403 when permissions are insufficient. |
| `422 Unprocessable Entity` | Malformed body — usually `event_type` missing, or `client_payload` isn't a JSON object. |
| `204` returned but no workflow run | `event_type` doesn't match `teams-message` exactly, or `teams-trigger.yaml` isn't on `develop` yet. |
| Run starts, fails immediately | "No change request text received" — `client_payload.changes_requested` is empty. Check the Compose expression and that the action name in the body matches. |
| Nothing happens when posting in Teams | **Option A:** check the flow's run history — the polling trigger may not have fired yet, or the condition filtered the message out. **Option B:** outgoing webhooks only fire on @mention. |
| Every message triggers a run | Add the Condition from step A2. |
| Runs trigger each other in a loop | The flow is reacting to its own posted messages — see A5. |
| Teams shows "unable to reach the app" | Relay didn't reply within 5 seconds, or replied without a valid message payload. |
| No "in progress" reply appears | Either `TEAMS_CALLBACK_URL` isn't set (both reply steps skip by design), or `client_payload.teams_message_id` is missing — the run logs a notice saying which. |
| Reply steps log a warning | The callback flow rejected the POST. Check its run history; a `202` is success for a Request trigger. |
| Replies post as new messages, not threaded | The flow is using **Post message in a chat or channel** instead of **Reply with a message in a channel**, or `message_id` is empty. |
| Pipeline replies re-trigger the pipeline | See A5 — the status replies are landing in the watched channel and passing the condition. |

---

## Security notes

- **Message text is untrusted input.** Anyone who can post in the channel controls it. The workflow only ever passes it through environment variables — keep it that way. Interpolating `${{ github.event.client_payload.changes_requested }}` directly into a `run:` block is a script-injection hole.
- **Channel membership becomes CI access.** Everyone who can post can start a run. Filter on message prefix and, if the workflow does anything sensitive, on the poster's identity too.
- `repository_dispatch` runs get a read-only `GITHUB_TOKEN` here (`permissions: contents: read`). Widen it only for what the workflow actually needs.
- **`TEAMS_CALLBACK_URL` is a credential.** The SAS signature is in the URL itself, so anyone who has it can post into your channel as the flow. Keep it in Actions secrets, and regenerate it (Power Automate → the flow → trigger → regenerate) if it leaks.

## Related

- [`teams-trigger.yaml`](teams-trigger.yaml) — the workflow this document configures
- [`JIRA_WEBHOOK_SETUP.md`](JIRA_WEBHOOK_SETUP.md) — the same `repository_dispatch` pattern, driven by Jira Automation

## References

- [Triggering a workflow — `repository_dispatch`](https://docs.github.com/en/actions/using-workflows/events-that-trigger-workflows#repository_dispatch)
- [REST API — Create a repository dispatch event](https://docs.github.com/en/rest/repos/repos#create-a-repository-dispatch-event)
- [Power Automate — Microsoft Teams connector](https://learn.microsoft.com/en-us/connectors/teams/)
- [Teams — Outgoing webhooks](https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-outgoing-webhook)
- [Security hardening for GitHub Actions — untrusted input](https://docs.github.com/en/actions/security-guides/security-hardening-for-github-actions#understanding-the-risk-of-script-injections)
