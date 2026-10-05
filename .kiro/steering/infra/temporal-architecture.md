---
inclusion: auto
description: >-
  Temporal workflow orchestration architecture for Whitbread Digital. Load when working
  with Temporal workflows, workers, UI access, or database configuration.
keywords:
  - temporal
  - workflow
  - temporal worker
  - temporal ui
  - temporal deployment
  - aurora postgresql
---

# Temporal Workflow Orchestration Architecture  ###

## Quick Reference

| **Component** | **Production (hulk/wanda)** | **Non-Production (dev/dit/sit/uat/perf)** |
|---------------|----------------|-------------------|
| Database | Serverless Aurora (shared) | Serverless Aurora (per-env cluster) |
| Auth | IAM (IRSA) | AWS Secrets Manager → ESO → `temporal-postgres-credentials` |
| UI Access | `temporal-ui.tooling.opera.whitbread.digital` via tools gateway | `temporal-ui.<env>.opera.whitbread.digital` via tools gateway |
| Namespace | `temporal` | `temporal` |
| Frontend API | `temporal-frontend.temporal.svc.cluster.local:7233` | Same |
| Storage class | N/A (Aurora) | N/A (Aurora) |

## Architecture Flow

```
Developer (office network) → rest-api-private Gateway → Temporal Web UI
Backend Service → Temporal Frontend → History/Matching → Serverless Aurora
Worker Service → Temporal Frontend (polls for tasks)
```

## Critical Rules

| **Pattern** | **Rule** | **Fix** |
|-------------|----------|---------|
| Worker connects to Temporal | Must use service mesh DNS | `temporal-frontend.temporal.svc.cluster.local:7233` |
| UI access needed | Must use rest-api-private gateway | VirtualService routes to `temporal-web:8080` |
| Production database config | Never use password | IAM auth with service account annotation |
| Non-prod database config | All non-prod use external Serverless Aurora (no in-cluster DB) | `postgresql.enabled: false` (set in `nonprod.yaml`); credentials via ESO `existingSecret`; per-env `connectAddr` in each `<env>.yaml` |
| Namespace | Always use "temporal" namespace | Not temporal-<env>, just `temporal` |

## Helm Chart Structure

**Parent chart:** `infra/temporal/Chart.yaml`

```
infra/temporal/
├── Chart.yaml                  # Dependency: temporal 1.6.0 (no in-cluster DB subchart)
├── Chart.lock
├── values/
│   ├── values.yaml             # Common config (all environments)
│   ├── nonprod.yaml            # Shared non-prod config (Aurora connection: ESO secret, pools, TLS)
│   ├── dev.yaml                # Dev-specific (Aurora endpoint, replicas, resources, UI host)
│   ├── dit.yaml                # DIT-specific (Aurora endpoint, replicas, autoscaling, UI host)
│   ├── sit.yaml                # SIT-specific (Aurora endpoint, replicas, autoscaling, UI host)
│   ├── uat.yaml                # UAT-specific (Aurora endpoint, replicas, autoscaling, UI host)
│   ├── perf.yaml               # PERF-specific (Aurora endpoint, higher replicas/autoscaling, UI host)
│   └── prod.yaml               # Production overrides (hulk/wanda)
├── templates/
│   ├── gateway.yaml            # Istio gateway for ExternalDNS (auto-creates DNS record)
│   ├── networkpolicy.yaml      # Frontend on 7233, web on 8080 (no DB policy — Aurora is external)
│   └── virtualservice.yaml     # Routes Web UI traffic via gateway to temporal-web:8080
└── charts/                     # Downloaded dependencies (gitignored)
```

**Key configuration in `values/values.yaml`:**
- `schema.useHelmHooks: false` — schema Job runs as regular resource (not pre-install hook)
- `schema.backoffLimit: 100` — retries until Aurora is reachable
- PodDisruptionBudgets on server and web (`minAvailable: 1`)
- SecurityContext on all pods (`runAsNonRoot`, `seccompProfile: RuntimeDefault`)
- NetworkPolicy enabled by default
- Istio sidecar injection via `podLabels`

**Deployment:**
```bash
# Via GitHub Actions (preferred):
# Trigger "Helm Deploy" workflow with chart=temporal, environment=dit

# Manual (requires cluster access):
cd infra/temporal
helm dependency build
helm upgrade --install temporal . \
  -n temporal \
  --values values/values.yaml \
  --values values/nonprod.yaml \
  --values values/dev.yaml \
  --wait --timeout 15m --atomic
```

## CI/CD Workflows

| Workflow | Trigger | What it does |
|----------|---------|--------------|
| `helm-validate.yaml` | PR with `infra/**` changes | Lint, template render, dry-run against cluster |
| `helm-deploy.yml` | Push to main (`infra/**`) or `workflow_dispatch` | Deploy to target environment |

**Deploy workflow inputs:**
- `chart` — chart name (e.g. `temporal`)
- `environment` — `dev`, `dit`, `sit`, `uat`, `perf`, `tooling`, `hulk`, `wanda`
- `fresh-install` — `true` to uninstall + delete PVCs before install (data loss!)

**Runner:** `dev-default-runner-scale-set` (self-hosted, pre-configured cluster access via `github-workflows-runners` ServiceAccount)

## Database Configuration

**All non-prod environments (dev/dit/sit/uat/perf) use Serverless Aurora.** The
in-cluster Bitnami PostgreSQL subchart has been removed entirely — there is no
in-cluster database, no `postgresql` chart dependency, no DB NetworkPolicy, and
no `wait-for-postgresql` init container. Each environment has its own Aurora
cluster, provisioned by the Infra team (dev migration tracked under **INFRA-1806**).

**Shared config (`values/nonprod.yaml`):**
- `postgresql.enabled: false` — applies to every non-prod env.
- Aurora connection settings common to all non-prod envs live here via the
  `&auroraSql` anchor: credentials via ESO `existingSecret`, connection pools,
  and TLS. Only the per-env writer endpoint differs.
- DB name `temporal`, user `temporal`. The password lives in **AWS Secrets
  Manager** and is synced into the `temporal-postgres-credentials` K8s Secret in
  the `temporal` namespace by **External Secrets Operator** (a
  `ClusterExternalSecret` via the `aws-secret-manager` `ClusterSecretStore`).
  Temporal reads it through `existingSecret` / `secretKey` — nobody sets the
  password in the chart (`password: ""` stays empty); to rotate it, update the
  value in AWS Secrets Manager and ESO re-syncs. The authoritative secret name,
  SM property, and refresh interval are owned by Infra — see **INFRA-1806**.
- **TLS:** Aurora enforces SSL (`rds.force_ssl`), so `tls.enabled: true`.
  `enableHostVerification: false` for now (the temporalio/server image does not
  trust the Amazon RDS CA until the RDS `global-bundle.pem` is mounted) —
  connections are encrypted but the server cert is not verified. Follow-up to
  enable host verification is tracked under **SRE-364**.
- Per-pod connection pools are kept small (default `maxConns: 10`, visibility
  `maxConns: 5`) to stay under the Aurora Serverless per-ACU connection ceiling;
  throughput is scaled out via server replicas rather than wider per-pod pools.

**Per-env writer endpoints (`values/<env>.yaml`, all eu-west-1, port 5432):**

| Env | Aurora writer endpoint | Server replicas |
|-----|------------------------|-----------------|
| dev | `temporal-dev.cluster-cdsnnftjn3jo.eu-west-1.rds.amazonaws.com` | 1 (SRE-352 stopgap; revert to 2) |
| dit | `temporal-dit.cluster-c1ujmevdpt50.eu-west-1.rds.amazonaws.com` | 2 |
| sit | `temporal-sit.cluster-cfp3ckubgukh.eu-west-1.rds.amazonaws.com` | 2 |
| uat | `temporal-uat.cluster-cfp3ckubgukh.eu-west-1.rds.amazonaws.com` | 2 |
| perf | `temporal-perf.cluster-cfp3ckubgukh.eu-west-1.rds.amazonaws.com` | 3 (autoscale target up to 10) |

> Note: the temporal 1.6.0 chart has no HPA template, so the `autoscaling` blocks
> in the env files are inert; `replicaCount` is the effective scaling control.

**Common to all Aurora clusters:**
- Each Aurora cluster must have BOTH databases: `temporal` and `temporal_visibility`.
- Reachability to Aurora is handled by security-group/routing config plus Istio
  egress; the chart does not add an egress NetworkPolicy (the namespace has no
  default-deny egress).
- **Ownership:** the Aurora clusters and their AWS Secrets Manager secrets are
  owned and managed by the Infra team. The Temporal chart only references the
  ESO-synced `temporal-postgres-credentials` K8s secret — it does not create or
  rotate credentials.

**Production (Aurora) — NOT YET CONFIGURED:**
> Platform team must provide: actual Aurora endpoint, IAM role ARN for IRSA, remove password-based auth. Prod uses IAM (IRSA), not CyberArk.

## Temporal UI Access

**URLs:**
- Dev: `https://temporal-ui.dev.opera.whitbread.digital`
- DIT: `https://temporal-ui.dit.opera.whitbread.digital`
- SIT: `https://temporal-ui.sit.opera.whitbread.digital`
- UAT: `https://temporal-ui.uat.opera.whitbread.digital`
- PERF: `https://temporal-ui.perf.opera.whitbread.digital`

**Gateway:** `ingress-tools-istio-ingressgateway` (office network access, ExternalDNS auto-creates DNS)

**How it works:** A Gateway + VirtualService route traffic from the tools ingress gateway to `<release-name>-web:8080`. ExternalDNS watches the Gateway host and creates the CNAME in Route53 automatically.

## Worker Service Pattern

> **Note:** No worker services exist yet — infrastructure is deployed first, workers will be added by application teams.

Java Spring Boot services in `backend/<squad>/services/<name>-worker/`:

**Required environment variables:**
- `TEMPORAL_HOST` → `temporal-frontend.temporal.svc.cluster.local:7233`
- `TEMPORAL_NAMESPACE` → squad/environment-specific namespace (see below)
- `TEMPORAL_TASK_QUEUE` → `<workflow-name>-tasks`

## Temporal Namespace Strategy

> **This only applies to NON-PROD environments (dev, dit, sit, uat, perf).**
> Production (hulk/wanda) MUST use a single shared namespace — all squads share the same Temporal namespace in production.

In non-prod, there is a **single Temporal instance per K8s cluster** (shared across squads and environments). Isolation is achieved using **Temporal namespaces** (a Temporal concept, not K8s namespaces):

| Use case | Temporal namespace | Example | Environment |
|----------|-------------------|---------|-------------|
| Production workflows | `default` (shared by all squads) | All workflows in one namespace | hulk/wanda only |
| Squad workflows (non-prod) | `<squad>` or `<squad>-<feature>` | `book-pay`, `arrive-stay-leave` | dev/dit/sit/uat/perf |
| Ephemeral environments | `<squad>-<ephemeral-id>` | `book-pay-pr-123` | dev/dit only |

**Rules:**
- **Production (hulk/wanda): all squads MUST share the same Temporal namespace** — do NOT create per-squad namespaces in prod
- Non-prod: each squad creates its own namespace(s) to avoid cross-contamination
- Ephemeral environments (feature branches, PR environments) MUST use a unique namespace
- Set retention per namespace (e.g. 72h for ephemeral, 30d for persistent)
- Workers MUST connect to their namespace via `TEMPORAL_NAMESPACE` env var

**Creating a namespace (non-prod only):**
```bash
kubectl exec -n temporal deployment/temporal-admintools -c admin-tools -- \
  temporal operator namespace create <namespace-name> --retention 72h
```

## Deployment Ownership

| **Component** | **Owner** |
|---------------|-----------|
| Aurora PostgreSQL, IAM roles, DNS records | Infrastructure (Ranjan Nalini) |
| Temporal Helm deployment, CI/CD workflows | SRE (Ross Williams, Dag Ivarsoy) |
| Worker services, workflows, activities | Squad teams |
| Runner RBAC in temporal namespace | CE team (INFRA-1821) |

## Related Documentation

- Ingress Gateways: #[[file:.kiro/steering/infra/ingress-gateways.md]]
- Meeting Notes: [2026-07-07 Temporal deployment solution](https://whitbreadis.atlassian.net/wiki/spaces/DSA/pages/5526159505)
- Temporal Helm Chart: https://github.com/temporalio/helm-charts
- Runner RBAC ticket: INFRA-1821
