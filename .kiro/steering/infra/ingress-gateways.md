# Ingress architecture overview

## High-level architecture

```
  ┌─────────────────────────────────────────┐
  │                Akamai                   │
  └───────────────────┬─────────────────────┘
                      │ HTTPS
                      ▼
  ┌─────────────────────────────────────────┐
  │              K8s Cluster                │
  │                                         │
  │  ┌──────────────────────────────────┐   │
  │  │         Istio Gateway            │   │
  │  └───────────────┬──────────────────┘   │
  │                  │                      │
  │                  ▼                      │
  │         ┌─────────────────┐             │
  │         │  VirtualService │             │
  │         │    (routing)    │             │
  │         └────────┬────────┘             │
  │                  │                      │
  │         ┌────────▼────────┐             │
  │         │   Services /    │             │
  │         │    Workloads    │             │
  │         └─────────────────┘             │
  └─────────────────────────────────────────┘
```

**Traffic flow:**

1. **Akamai** — sits at the edge, handling CDN caching, WAF rules, and DDoS mitigation. Forwards clean traffic to the cluster's Istio Gateway.
2. **Istio Gateway** — the cluster-side entry point. Terminates TLS, implements request authentication and authorization, matches hosts/ports to route rules. The definitive source of truth for the supported gateways is [opera-services/gateways.yaml](https://github.com/whitbread-eos/CE-Common-gitops/blob/main/opera-services/gateways.yaml) and [apollo-router/gateway.yaml](https://github.com/whitbread-eos/CE-Common-gitops/blob/main/apollo-router/gateway.yaml)
3. **VirtualService** — Istio routing rules that map incoming requests to the correct internal service based on path, headers, or weights.

## Frontend page and SSR routing
Frontend page and SSR requests are routed via the following gateways:
* `pi` - premierinn.com (direct channel) website
* `ccui` - ccui.premierinn.com (Call Center UI) website
* `innbusiness` - premierinnbusiness.com (business channel) website

These gateways do not enforce any authentication/authorization and must be used for routing Web FE application page and SSR traffic only.

## Web CSR and Mobile GraphQL routing
> [!NOTE]
> This only applies to the client side traffic. SSR traffic doesn't use the gateway, see the relevant section

Client-side rendering traffic from browsers and mobile apps. All requests enter via Akamai and
are routed through the `opera-apollo-router` Istio Gateway into the cluster.

This gateway does not enforce any authentication/authorization and must be used for routing client-side GraphQL requests from Premier Inn Web and Mobile frontends only.


```
  Browser CSR/ Mobile App
         │
         │ HTTPS (GraphQL requests)
         ▼
  ┌─────────────┐
  │   Akamai    │
  └──────┬──────┘
         │
         ▼
  ┌─────────────────────────────────────────────────────┐
  │                    K8s Cluster                      │
  │                                                     │
  │  ┌──────────────────────────────────────────────┐   │
  │  │       opera-apollo-router Istio Gateway      │   │
  │  └───────────────────┬──────────────────────────┘   │
  │                      │                              │
  │                      ▼                              │
  │             ┌─────────────────┐                     │
  │             │  Apollo Router  │                     │
  │             │  (federation)   │                     │
  │             └────────┬────────┘                     │
  │                      │                              │
  │                      ▼                              │
  │          ┌───────────────────────┐                  │
  │          │   GraphQL Subgraph    │                  │
  │          │ (REST adapter layer)  │                  │
  │          └───────────┬───────────┘                  │
  │                      │                              │
  │                      ▼                              │
  │          ┌───────────────────────┐                  │
  │          │   Backend REST APIs   │                  │
  │          └───────────────────────┘                  │
  └─────────────────────────────────────────────────────┘
```

---

## Web SSR GraphQL routing

Server-side rendering in `premierinn.com` and `premierinnbusiness.com` Next.js apps. During SSR
the app pods call Apollo Router directly via the Envoy sidecar (service mesh). No ingress
gateways are involved — traffic never leaves the cluster.

```
  ┌─────────────────────────────────────────────────────┐
  │                    K8s Cluster                      │
  │                                                     │
  │  ┌────────────────────────────────────────────┐     │
  │  │   Next.js SSR Pod                          │     │
  │  │   (premierinn.com / premierinnbusiness.com) │     │
  │  │                                            │     │
  │  │   ┌──────────────────┐                     │     │
  │  │   │  Envoy sidecar   │  (Istio service mesh)│    │
  │  │   └────────┬─────────┘                     │     │
  │  └────────────┼───────────────────────────────┘     │
  │               │ in-cluster (service mesh)           │
  │               ▼                                     │
  │      ┌─────────────────┐                            │
  │      │  Apollo Router  │                            │
  │      │  (federation)   │                            │
  │      └────────┬────────┘                            │
  │               │                                     │
  │               ▼                                     │
  │   ┌───────────────────────┐                         │
  │   │   GraphQL Subgraph    │                         │
  │   │ (REST adapter layer)  │                         │
  │   └───────────┬───────────┘                         │
  │               │                                     │
  │               ▼                                     │
  │   ┌───────────────────────┐                         │
  │   │   Backend REST APIs   │                         │
  │   └───────────────────────┘                         │
  └─────────────────────────────────────────────────────┘
```

**Key difference from CSR:** SSR requests originate inside the cluster and reach Apollo Router
via Envoy sidecar (service mesh). The `opera-apollo-router` Istio Gateway and Akamai are
bypassed entirely — they only handle external ingress.

## Public REST API Routing
In specific cases Web CSR and Mobile use direct REST calls rather than GraphQL. These are routed via `opera-api` gateway directly to the backends.

This gateway does not enforce any authentication/authorization and must be used for routing client-side REST requests from Premier Inn Web and Mobile frontends only. It MUST NOT be used for either developer access or 3rd-party integration.

```
  Browser CSR/ Mobile App
         │
         │ HTTPS (REST requests)
         ▼
  ┌─────────────┐
  │   Akamai    │
  └──────┬──────┘
         │
         ▼
  ┌─────────────────────────────────────────────────────┐
  │                    K8s Cluster                      │
  │                                                     │
  │  ┌──────────────────────────────────────────────┐   │
  │  │       opera-api Istio Gateway                │   │
  │  └───────────────────┬──────────────────────────┘   │
  │                      │                              │
  │                      ▼                              │
  │          ┌───────────────────────┐                  │
  │          │   Backend REST APIs   │                  │
  │          └───────────────────────┘                  │
  └─────────────────────────────────────────────────────┘
```

## Private REST API Routing
Where a REST API endpoint is not used directly by the frontends, but must be exposed for internal (e.g. development and testing) access, a separate `rest-api-private` gateway must be used.

This gateway does not enforce any authentication/authorization and must be used for internal routing only. It MUST NOT be used for 3rd-party integration.

```
  Browser CSR/ Mobile App
         │
         │ HTTPS (REST requests)
         ▼
  ┌─────────────┐
  │   Akamai    │
  └──────┬──────┘
         │
         ▼
  ┌─────────────────────────────────────────────────────┐
  │                    K8s Cluster                      │
  │                                                     │
  │  ┌──────────────────────────────────────────────┐   │
  │  │       rest-api-private Istio Gateway         │   │
  │  └───────────────────┬──────────────────────────┘   │
  │                      │                              │
  │                      ▼                              │
  │          ┌───────────────────────┐                  │
  │          │   Backend REST APIs   │                  │
  │          └───────────────────────┘                  │
  └─────────────────────────────────────────────────────┘
```

## Secure REST API routing for 3rd-party integrations
Where a REST API must be exposed for a 3rd party integration a separate `auth-api` must be used. All requests routed via this gateway must be authenticated and authorized with an approved identity service (Auth0 at the time of writing).

> [!NOTE]
> Even if the same API is already exposed via the public REST gateway, any 3rd party integration must be implemented with the secure REST gateway. Never assume that a public API will always stay public — it can be removed from public access at any time. Publishing the API on the secure gateway, on the other hand, guarantees continued access for the 3rd party.

```
  3rd-party server
         │
         │ mTLS (REST requests)
         ▼
  ┌──────────────────────────────┐
  │   Akamai (terminate mTLS)    │
  └──────┬───────────────────────┘
         │
         ▼
  ┌─────────────────────────────────────────────────────┐
  │                    K8s Cluster                      │
  │                                                     │
  │  ┌──────────────────────────────────────────────┐   │
  │  │       auth-api Istio Gateway                 │   │
  │  └───────────────────┬──────────────────────────┘   │
  │                      │                              │
  │                      ▼                              │
  │          ┌───────────────────────┐                  │
  │          │   Backend REST APIs   │                  │
  │          └───────────────────────┘                  │
  └─────────────────────────────────────────────────────┘
```