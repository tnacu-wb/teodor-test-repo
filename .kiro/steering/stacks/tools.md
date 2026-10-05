---
inclusion: fileMatch
fileMatchPattern: "tools/**"
---

# Tools Stack Conventions

Applies to everything under `tools/` — internal operational utilities and developer tools.
These are not customer-facing services: they are run by the SRE and platform teams to
observe, seed, or administer the estate.

## What Belongs Here

- Long-running operational daemons (stream processors, exporters, sync jobs).
- Internal web tools used by engineers and testers.
- Anything that is deployed as a container but is **not** part of the Java estate.

A tool is not a squad service. Do not put it under `backend/<squad>/services/`, do not give it
a Maven POM, and do not add it to the reactor — the root `pom.xml` must stay Java-only.

## Layout

**Python tools live at `tools/python/<name>/`.** This is not optional: the generic CI
(`ci-python-tools.yaml` + `detect-changed-python-tools.sh`) only discovers tools under the
`tools/python/` prefix, so a Python tool placed anywhere else in `tools/` gets **silently zero
CI** — no test run, no image build, and no failure to tell you. See the CI section below.

Non-Python tools that predate this convention (e.g. `opera-ohip-app`) sit flat at
`tools/<name>/` and carry their own bespoke build workflow. Do not use them as a template for
new Python tools.

## Toolchain Independence

Each tool owns its own language, build tool, and test runner. There is no shared build for
`tools/`; a change to one tool must never require building another.

Current members:

| Tool | Language | Build / run |
|------|----------|-------------|
| `opera-ohip-app` | TypeScript / Next.js | `npm`, multi-stage Dockerfile |
| `ddb-stream-basket-to-dt` | Python 3.14 | `uv`, single-stage Dockerfile |

## Python Conventions

`tools/` is the only place Python lives in this monorepo. When adding or changing a Python tool:

- **Package manager is `uv`, never `pip`.** Dependencies are declared in `pyproject.toml` and
  pinned in a committed `uv.lock`. Upgrade in two steps: `uv lock --upgrade` (commit the lock
  file), then `uv sync` (updates the local `.venv/`, which is not committed). `uv pip install`
  alone changes only the venv and leaves the lock file stale.
- **Tests use the standard library `unittest`** — no pytest. Run them with
  `uv run python -m unittest <test_module>` from the tool's own directory.
- **Lint with pylint** against the tool's own `.pylintrc` (max line length 120).
- **Never commit `.venv/`, `__pycache__/`, or build logs.**
- Run the tests after every change; these tools have no integration environment to catch
  regressions later.

## Containers

- Every tool ships a Dockerfile at its own root, built with that directory as the build context.
- Run as a **non-root** user (uid/gid 9001 is the convention here) with `runAsNonRoot`,
  `seccompProfile: RuntimeDefault`.
- Base images are being moved behind the ECR pull-through cache
  (`327347623470.dkr.ecr.eu-west-1.amazonaws.com/ecr-public/docker/library/...`) — see SRE-336.
  Prefer that form for new work rather than pulling from Docker Hub or a public registry.
- Local integration harnesses use a `compose.yaml` in the tool's directory, wiring the tool
  against emulators (DynamoDB Local, WireMock) rather than real environments.

## CI

The backend orchestrator (`ci-be.yaml`, via `detect-all.sh` / `detect-changed-eph.sh`)
classifies only `backend/*/services|libs`, `graphql/**` and `infra/<chart>/` — it does **not**
build anything under `tools/`.

**Python tools are the exception**, and are built generically: `ci-python-tools.yaml` (with
`detect-changed-python-tools.sh`) triggers on any change under `tools/python/**`, discovers
which tool directories changed, and derives each tool's details from its own files — Python
version from `.python-version`, image name as `whitbreaddigital/<pyproject name>`, tests via
`uv run python -m unittest`. Dropping a new tool under `tools/python/<name>/` (with a
`pyproject.toml`) gets it tested on PRs, and image-built + ECR-pushed on merge to main if it
ships a `Dockerfile` — **no workflow edit required**.

Non-Python tools (e.g. `opera-ohip-app`) still need their own path classification and build
workflow. Check the current state of the workflows before assuming a given tool is tested on
merge.

## Deployment

Tools are deployed by Flux from the gitops repositories (`microservices-gitops`,
`microservices-gitops-prod`), not from this repo. Deployment manifests, image tags, and
environment-specific configuration live there — this repo owns source, tests, and (where wired
up) the image build. Never add environment credentials or per-cluster config to a tool's
source tree.
