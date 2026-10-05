---
inclusion: fileMatch
fileMatchPattern: "tools/python/ddb-stream-basket-to-dt/**"
---

# Project Structure

## Layout

A deliberately flat, single-module tool. There is no package directory and no framework.

```
tools/python/ddb-stream-basket-to-dt/
├── basket_stream_to_dynatrace.py       # entire application (~410 lines)
├── test_basket_stream_to_dynatrace.py  # unittest suite (~420 lines)
├── test-data/                          # 1.json, 2.json — DDB batch-write fixtures
├── compose.yaml                        # local integration harness
├── Dockerfile                          # single-stage, python:3.14-alpine + uv
├── pyproject.toml / uv.lock            # dependencies (pinned)
├── .python-version                     # 3.14
├── .pylintrc                           # lint config (max line length 120)
└── README.md                           # usage, architecture, log examples
```

Keep it flat. If this tool grows enough to need packages, that is a deliberate restructure,
not something to do incidentally.

## Process Model

Multi-process, one process per stream shard:

- **Parent** (`start_watching`) polls `list_open_shards` every 10 seconds and spawns a
  `multiprocessing.Process` running `shard_watcher` for each shard it has not already seen.
  It installs SIGTERM/SIGINT handlers that set a shared `shutdown_event`, then joins children
  (15s) and terminates any stragglers (5s).
- **Child** (`shard_watcher`) owns one shard: get iterator, `GetRecords`, summarise, POST,
  advance. Each child builds its own boto3 `dynamodbstreams` client — clients are not
  fork-safe, so never create one in the parent and share it.
- Shards discovered on the **first** loop start at `LATEST`; shards discovered later (from
  table splits or scaling) start at `TRIM_HORIZON`, so a new shard is not missed while the
  process is running.

## Control Flow in a Shard Watcher

1. `get_next_records` returns up to 1000 records plus the next iterator.
2. Each record goes through `extract_relevant_fields`; records yielding no fields are logged
   as a warning and skipped.
3. If there are summaries, `post_to_dynatrace` is called with the whole batch. The batch size
   is whatever DynamoDB returned — there is no separate batching layer.
4. **The iterator advances only on success.** On failure the iterator is left alone, the error
   count increments, and the loop sleeps `2 ** error_count` seconds before retrying the same
   records.
5. Once the error count exceeds 5 (i.e. the sixth consecutive failure on that shard), the child
   sets the shared `shutdown_event`, which brings the whole process group down with exit code
   `70`. Kubernetes then restarts the pod — the design assumes a restart is a better response
   to sustained Dynatrace failure than an ever-growing backlog.
6. When a batch is empty the iterator advances and the loop sleeps 0.5s.

This ordering is the correctness core of the tool. Any refactor that advances the iterator
before a confirmed POST silently turns at-least-once delivery into best-effort.

## Function Map

| Function | Role |
|----------|------|
| `main` | Arg parsing, log-level precedence, startup liveness POST, then hands off |
| `start_watching` | Shard discovery loop, process supervision, signal handling |
| `shard_watcher` | Per-shard read/summarise/post loop and backoff |
| `extract_relevant_fields` | The allow-list summariser and event-type classifier |
| `post_to_dynatrace` | Business Events POST, plus `fail` / `localtest` test hooks |
| `validate_dt_env` | Rejects anything outside `[A-Za-z0-9-]+` before it reaches a URL |
| `sanitize_log_message` | Strips newlines and truncates before logging remote content |
| `list_all_shards` / `list_open_shards` / `is_open_shard` | Paginated shard discovery |
| `get_shard_iterator` / `get_next_records` | Thin boto3 wrappers, injectable client |
| `get_latest_stream_arn` | Resolves table name to stream ARN; fails if streams are off |

Boto3 wrappers all take an optional `client=` argument. That is the seam the tests use — keep
it when adding new AWS calls rather than reaching for a mocking framework.

## Testing Patterns

- **Unit tests** (`test_basket_stream_to_dynatrace.py`) are entirely focused on
  `extract_relevant_fields` — the summarisation and classification logic, which is where the
  business risk sits. Cases cover each basket status, all channels, payment options, decimal
  and numeric-string coercion, missing/empty `NewImage`, REMOVE events, partial data, nested
  error variants, null-typed attributes, and an explicit no-PII assertion.
- **Integration** is `compose.yaml`: DynamoDB Local, a table created with
  `StreamEnabled=true, StreamViewType=NEW_IMAGE`, the tool built from source, then two
  `batch-write-item` calls from `test-data/` to fire records through the pipeline.
  `DT_API_KEY=localtest` makes the tool log what it would have posted instead of calling
  Dynatrace; `DT_ENV=fail` forces the failure path so backoff and shutdown can be exercised.
- There is no test coverage of the process supervision or shard-splitting logic. Changes there
  are verified by the compose harness and by reading the code — be correspondingly careful.

## Gotchas

- Only string-typed (`S`) DynamoDB attributes are read. Values that parse as integers are
  coerced with `int()`, so `totalCost` arrives in Dynatrace as a number when it looks like one
  and a string when it does not.
- The README claims `event.type` is always `basket.updated`. It is not — the code derives
  `<prefix>.<status>`. Trust the code and the tests over the README.
- The `Dockerfile` healthcheck is `sleep 5`, a deliberate hack so compose dependents wait. It
  is not a real liveness signal; the process either runs or exits.
