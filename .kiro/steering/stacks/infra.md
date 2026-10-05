---
inclusion: fileMatch
fileMatchPattern: "{helm/**,**/infra/**,.github/workflows/**}"
---

# Infra Stack Conventions

Applies to all infrastructure config under `helm/`, per-service `infra/` folders,
and CI workflows under `.github/workflows/`. Detailed topic docs live in
`.kiro/steering/infra/`.

## Runtime Platform

- **Kubernetes** — all services run as K8s workloads.
- **Istio service mesh** — Envoy sidecars injected into every pod; no mTLS enforced.
- **Helm** — the standard packaging and deployment mechanism for services and shared config.

## Helm Conventions

- Charts live under `infra/<chart-name>/` with `Chart.yaml`, `templates/`, and `values/`.
- Values are layered: `values.yaml` (base) → `nonprod.yaml` (shared non-prod) → `<env>.yaml` (per-environment).
- Production values use: `values.yaml` → `prod.yaml`.
- Use `{{ required }}` or conditional guards (`{{- if and ... }}`) to fail fast on missing required values rather than rendering invalid manifests.

## Pod Security (mandatory for all new workloads)

Every new Deployment/StatefulSet MUST include:

- **Non-root execution**: `runAsNonRoot: true`, explicit `runAsUser`/`fsGroup`.
- **Seccomp profile**: `seccompProfile.type: RuntimeDefault`.
- **Drop all capabilities**: `capabilities.drop: [ALL]`, `allowPrivilegeEscalation: false`.
- **Read-only root filesystem**: `readOnlyRootFilesystem: true` with `emptyDir` volumes for writable paths (e.g. `/tmp`, app cache dirs).
- **No service account token**: `automountServiceAccountToken: false` unless the workload explicitly needs K8s API access.
- **Istio sidecar injection**: via pod annotation `sidecar.istio.io/inject: "true"` (not label).

## High Availability (mandatory for all new workloads)

- **Minimum 2 replicas** for any workload serving traffic.
- **PodDisruptionBudget**: always include a PDB with `minAvailable: 1` (or appropriate for the replica count).
- **Topology spread**: use `topologySpreadConstraints` with `topologyKey: kubernetes.io/hostname` to spread pods across nodes.
- **Rolling update strategy**: `maxUnavailable: 0`, `maxSurge: 1` to avoid downtime during deploys.

## Network Security (mandatory for all new workloads)

- **NetworkPolicy**: every workload MUST have a NetworkPolicy restricting ingress to only the expected sources (typically the Istio ingress gateway namespace `istio-system`).
- Do not leave pods open to cluster-wide traffic by default.

## Secrets Management

- Secrets are managed via **CyberArk** and synced to K8s using **External Secrets Operator**.
- SecretStore: `conjur-digital-app-secrets` (per-namespace, kind: SecretStore).
- Remote ref format: `data/vault/<safe-name>/<account-name>/address`.
- ExternalSecret `refreshInterval` should be `1m`.
- Never commit real secret values to git. App keys that function as credentials should also be sourced from CyberArk.
- Use `optional: true` on `secretKeyRef` env vars if the app should start without secrets (e.g. during initial deployment before sync completes).

## Health Checks

- Every workload MUST define both `livenessProbe` and `readinessProbe`.
- Use appropriate `initialDelaySeconds` to avoid premature restarts during startup.
- Health endpoints should be lightweight (no DB calls, no external dependencies).

## Resource Management

- Always set `resources.requests` (cpu + memory) and `resources.limits` (at minimum memory).
- CPU limits are optional but memory limits are mandatory to prevent OOM kills affecting neighbours.

## CI

- Helm charts are validated on PR via `helm-validate.yaml` (lint + template).
- Deployments are triggered via `helm-deploy.yml` (workflow_dispatch or push to main under `infra/**`).
- Available environments: `dev`, `dit`, `sit`, `uat`, `perf`, `tooling`, `hulk`, `wanda`.
- Runner label pattern: `<environment>-default-runner-scale-set`.

## Adding a New Gateway

- Internal tools on the ce-tooling cluster use their own Gateway resource with selector `istio: ingress-tools-istio-ingressgateway`.
- ExternalDNS automatically creates DNS records from Gateway/VirtualService hosts.
- TLS is terminated at the ALB level; Istio Gateway uses `protocol: HTTP` on port 443.
- See `infra/opera-ohip-app/templates/virtualservice.yaml` or `infra/temporal/templates/gateway.yaml` as reference implementations.
