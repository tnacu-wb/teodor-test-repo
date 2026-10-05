---
inclusion: fileMatch
fileMatchPattern: "graphql/**"
---

# Opera Apollo Subgraphs — Tech

## Runtime

| Property | Value |
|----------|-------|
| Language | TypeScript 5.7 (strict mode) |
| Runtime | Node.js 22 |
| Module system | CommonJS (`"module": "commonjs"` in tsconfig) |
| Target | ES2023 |

## Key Dependencies

| Package | Version | Purpose |
|---------|---------|---------|
| `@apollo/server` | ^5.5.1 | Apollo Server 5 — HTTP server for GraphQL subgraphs |
| `@apollo/subgraph` | ^2.9.3 | Federation directives and `buildSubgraphSchema()` |
| `express` | ^4.21.2 | HTTP framework; hosts Apollo Server as middleware |
| `axios` | ^1.15.0 | HTTP client for outbound REST calls to backend services |
| `graphql` | ^16.9.0 | GraphQL reference implementation (peer dependency) |
| `graphql-tag` | ^2.12.6 | Template literal tag for parsing `.graphql` SDL at build time |
| `pino` | ^9.5.0 | Structured JSON logger |
| `pino-pretty` | ^13.0.0 | Human-readable log formatting for local development |
| `dotenv` | ^16.4.6 | Loads `.env.local` environment variables in development |
| `cors` | ^2.8.5 | Express CORS middleware |

## Observability Stack

| Package | Purpose |
|---------|---------|
| `@opentelemetry/sdk-node` | OpenTelemetry Node.js SDK bootstrap |
| `@opentelemetry/exporter-prometheus` | Prometheus metrics endpoint (`/metrics`) |
| `@opentelemetry/exporter-trace-otlp-http` | OTLP/HTTP trace export to collector |
| `@opentelemetry/instrumentation-express` | Auto-instrument Express routes |
| `@opentelemetry/instrumentation-http` | Auto-instrument outbound HTTP calls |
| `@opentelemetry/instrumentation-graphql` | Auto-instrument GraphQL resolvers |
| `@opentelemetry/auto-instrumentations-node` | Umbrella auto-instrumentation package |

## Dev Dependencies (notable)

| Package | Purpose |
|---------|---------|
| `jest` + `ts-jest` | Test runner with TypeScript support |
| `axios-mock-adapter` | Mock Axios responses in unit tests |
| `eslint` + `eslint-plugin-prettier` | Linting with Prettier integration |
| `prettier` | Code formatting |
| `typescript` | TypeScript compiler |
| `@apollo/rover` | Schema validation CLI (used in CI) |

## Build Pipeline

1. `prettier --write .` — format all source files
2. `tsc` — compile TypeScript to `dist/`
3. `rsync -a --include='*/' --include='*.graphql' --exclude='*' src/ dist/` — copy schema
   files to output directory (TypeScript compiler doesn't handle `.graphql` files)

## Environment Variables (runtime)

| Variable | Description |
|----------|-------------|
| `PORT` | HTTP listen port (default `4000`) |
| `NODE_ENV` | `production` / `development` |
| `OTEL_EXPORTER_OTLP_ENDPOINT` | OpenTelemetry collector endpoint |
| `OTEL_SERVICE_NAME` | Service name for traces and metrics |
| Backend service URLs | One per downstream API (configured per-environment) |
