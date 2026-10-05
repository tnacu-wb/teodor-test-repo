---
inclusion: fileMatch
fileMatchPattern: "graphql/**"
---

# GraphQL Stack Conventions

Applies to all modules under `graphql/`. Module-specific detail lives in
`graphql/<module>/{product,structure,tech}.md`.

## Language & Runtime

- TypeScript 5.7 (strict mode, target `es2023`, module `commonjs`)
- Node.js 22 (pinned in `engines` field)
- Apollo Server 5
- Apollo Subgraph 2 (`@apollo/subgraph`)

## Build System

- **Package manager:** npm (not Yarn)
- **Available scripts:**
  - `build` — Prettier format + TypeScript compile + rsync `.graphql` to `dist/`
  - `test` — Build then run Jest (local development only; CI does not run tests)
  - `lint` — ESLint on `src/**/*.{js,ts}`
  - `format:check` — Prettier check without write
- **CI** (`build-graphql.yaml`) runs: install Rover CLI → schema compatibility check
  (only when subgraph schema files at `src/apollo/subgraphs/**/schema/schema.graphql`
  changed) → `npm install` → `npm run build` → artifact upload. Tests are not executed
  in CI (matches the original source repo).

```bash
cd graphql && npm install
npm run build          # CI runs this
npm test               # local only — not part of CI
npm run lint
npm run format:check
```

## Testing

- Jest with `ts-jest` preset
- `axios-mock-adapter` for HTTP mocking
- Tests live in `src/tests/`

## Code Style

- **Prettier:** default config in `.prettierrc`
- **ESLint:** TypeScript + Prettier integration (`eslint-config-prettier`,
  `eslint-plugin-prettier`)

## Observability

- OpenTelemetry SDK (`@opentelemetry/sdk-node`)
- Prometheus metrics exporter (`@opentelemetry/exporter-prometheus`)
- OTLP trace exporter (`@opentelemetry/exporter-trace-otlp-http`)
- Express, HTTP, and GraphQL auto-instrumentation

## Layout

```
graphql/
└── src/
    ├── index.ts                            # Entry point
    ├── tests/                              # Jest test suites
    └── apollo/
        ├── client/                         # Axios HTTP client wrappers
        ├── config/                         # Apollo Server configuration
        ├── exception/                      # Error handling
        ├── log/                            # Pino logger setup
        ├── middleware/                     # Express middleware
        ├── pipeline/                       # Request pipeline utilities
        ├── subgraphs/                      # Federated subgraphs
        │   └── <subgraph-name>/
        │       ├── resolvers.ts
        │       ├── dataSources.ts
        │       └── schema/
        │           └── schema.graphql
        ├── telemetry/                      # OpenTelemetry setup
        └── utils/                          # Shared utilities
```
