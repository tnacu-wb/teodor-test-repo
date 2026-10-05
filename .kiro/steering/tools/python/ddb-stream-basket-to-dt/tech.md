---
inclusion: fileMatch
fileMatchPattern: "tools/python/ddb-stream-basket-to-dt/**"
---

# Tech Stack

## Language & Runtime

- Python 3.14 (`requires-python = ">=3.14"`, `.python-version` pins 3.14)
- No framework. Standard library `multiprocessing`, `argparse`, `logging`, `signal`.

## Build System

- `uv` for dependency resolution and execution. Never `pip` — see `stacks/tools.md`.
- Dependencies in `pyproject.toml`, pinned in the committed `uv.lock`.

| Dependency | Purpose |
|------------|---------|
| `boto3` | DynamoDB and DynamoDB Streams clients |
| `requests` | Dynatrace Business Events POST |
| `urllib3` | Pinned transitively for CVE currency |

Boto3 clients are built with an adaptive retry config (`max_attempts: 5`) and
`max_pool_connections=50` in the shard watchers.

## Testing

| Tool | Purpose |
|------|---------|
| `unittest` (stdlib) | Unit tests for the summariser |
| Docker Compose + DynamoDB Local | End-to-end stream test with no AWS or Dynatrace involved |
| `pylint` | Lint, configured by `.pylintrc` |

## Common Commands

Run from `tools/python/ddb-stream-basket-to-dt/`:

```bash
# Install / sync dependencies
uv sync --locked --no-build

# Unit tests — run these after every change
uv run --locked --no-build python -m unittest test_basket_stream_to_dynatrace.py

# Run against a real table
export DT_API_KEY=<token>
uv run basket_stream_to_dynatrace.py <table-name> --dt-env whitbread-non-prod --log-level DEBUG

# Full local integration run (DynamoDB Local, no Dynatrace calls)
docker compose up --build

# Exercise the Dynatrace failure path (backoff + exit 70)
DT_ENV=fail docker compose up --build

# Lint
uv run pylint basket_stream_to_dynatrace.py

# Upgrade dependencies (two steps — the lock file is what ships)
uv lock --upgrade && uv sync && uv pip check
```

## Runtime Configuration

| Input | Form | Notes |
|-------|------|-------|
| table | positional arg, supplied as `DDB_TABLE` env var by the container `CMD` | The deployments pass a full table ARN |
| `--dt-env` / `DT_ENV` | Dynatrace environment name | Default `whitbread-non-prod`; validated against `[A-Za-z0-9-]+` before use in a URL |
| `DT_API_KEY` | env var, **required** | From the `ddb-stream-basket-to-dt` k8s secret in cluster |
| `--log-level` / `LOG_LEVEL` / `--debug` | log verbosity | Precedence: `--log-level` > `LOG_LEVEL` > `--debug` > `INFO` |

Special values used by tests: `DT_API_KEY=localtest` logs instead of posting;
`DT_ENV=fail` forces every POST to fail.

AWS credentials come from the standard boto3 chain — in cluster that is IRSA via the
`ddb-stream-basket-to-dt-account` service account, never static keys.

## Exit Codes

| Code | Meaning |
|------|---------|
| `0` | Clean shutdown after SIGTERM/SIGINT |
| `1` | Could not resolve the table's stream ARN (table missing, or streams not enabled) |
| `2` | Invalid `--dt-env` value |
| `70` | Dynatrace unreachable — either the startup liveness POST failed, or a shard exceeded its consecutive-failure budget |

Exit 70 is the expected way this tool reacts to sustained Dynatrace failure. A pod in
`CrashLoopBackOff` with exit 70 means "Dynatrace ingest is broken", not "the tool is broken".

## Container

- Single-stage `python:3.14-alpine`, `uv` copied from `ghcr.io/astral-sh/uv`.
- Runs as uid/gid 9001 (`runner`), `PYTHONDONTWRITEBYTECODE`, `PYTHONUNBUFFERED`,
  `UV_COMPILE_BYTECODE`, `UV_NO_DEV`.
- `pip` is deliberately removed from every location in the image (venv, system Python, uv-managed
  Python, `ensurepip`) to clear a Prisma finding. Do not reintroduce it — install with `uv`.
- `BUILD_TIMESTAMP` build arg is written to `/build-timestamp.txt` and surfaces on every event
  as `build.timestamp`. Preserve this when changing the build: it is how a data change in
  Dynatrace gets attributed to a code version.
- The `ghcr.io/astral-sh/uv` stage has no ECR Public equivalent and is an open question in
  SRE-336 (mirror `uv` through the base-images pipeline, or install it another way). Do not
  swap it for a GHCR pull-through cache — that reintroduces a credential to manage.

## CI/CD

Deployment is Flux + Kustomize from the gitops repos, **not** Helm:

- `ms-deployment/ddb-stream-basket-to-dt/` in `microservices-gitops` (nonprod) and
  `microservices-gitops-prod` (prod) holds the Deployment: 1 replica, `Recreate` strategy.
- Per-cluster Flux `Kustomization`s pin the image tag via `postBuild.substitute` and patch env
  and resources per environment.
- `Recreate` (not rolling) is correct here: two replicas of the same shard watcher would double
  every event.

Image tags are hand-edited today in both nonprod and prod. Automating that, along with building
this tool in the monorepo at all, is SRE-338 — check whether it has landed before assuming a
merge here produces a deployable image.
