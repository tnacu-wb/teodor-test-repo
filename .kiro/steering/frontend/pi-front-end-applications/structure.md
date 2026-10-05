---
inclusion: fileMatch
fileMatchPattern: "frontend/pi-front-end-applications/**"
---

# Project Structure

Yarn workspaces monorepo orchestrated by Lerna + Nx. Three Next.js apps consume a shared Atomic Design component catalog.

## Top-Level Layout

```
apps/next-apps/          # Deployable Next.js applications
  premier-inn/           # Public booking site
  business-booker/       # Business customer booking
  ccui/                  # Contact Centre agent UI
pi-components-catalog/   # Shared @whitbread-eos/* packages
docs/                    # Documentation
scripts/                 # Repo automation scripts
veracode/                # Security scan config
.changeset/              # Changesets for versioning
.husky/                  # Git hooks (pre-commit, commit-msg)
```

Workspace globs (root `package.json` / `lerna.json`):
`apps/next-apps/*`, `pi-components-catalog/*`.

## Shared Component Catalog (`pi-components-catalog/`)

Organized by Atomic Design and concern. Each is its own `@whitbread-eos/*` package with its own `package.json`, `tsconfig.json`, `.eslintrc.json`, `jest.config.js`, and `src/`.

- `atoms/` — smallest UI primitives (Chakra UI wrapper library, icons via SVGR)
- `molecules/` — composed components
- `organisms/` — larger composite components
- `layout/` — layout components
- `utils/` — shared helpers (`@whitbread-eos/utils`)
- `api/` — GraphQL types and API layer (`@whitbread-eos/api`); generated types land in `api/src/types/graphql.ts`
- `config/` — shared Babel/Jest/Prettier/TS config presets

Dependency direction: apps depend on catalog packages; within the catalog, higher tiers (organisms) depend on lower tiers (atoms, utils). Don't introduce reverse dependencies.

## App Structure (`apps/next-apps/<app>/`)

Next.js Pages Router apps. Typical `src/` layout (premier-inn):

```
src/
  pages/         # Next.js routes + API routes under pages/api/
  components/    # App-specific components
  hooks/         # Custom React hooks
  lib/           # Library/integration code
  store/         # Client state
  utils/         # App-specific helpers
  page-helper/   # Per-page logic helpers
  types/         # App-specific TypeScript types
  mocks/         # Test/dev mocks
  firebase/      # Firebase integration
  middleware.ts  # Next.js middleware
__tests__/       # Test suites
helm/            # Helm chart values for deployment
public/          # Static assets
```

Other app files: `next.config.js`, `next-i18next.config.js`, `theme.ts` (Chakra theme), `.env.local` (gitignored; see `.env.local.sample`), `sonar-project.properties`.

## Conventions

- **Import aliases:** Use `~`-prefixed paths within an app (`~components`, `~hooks`, `~utils`, `~store`, `~types`, `~page-helper`, `~queries`, `~services`, `~public`). Import shared code from `@whitbread-eos/*` packages, not deep relative paths.
- **Tests:** Co-located or under `__tests__/`, named `*.test.ts(x)`. Jest + Testing Library; accessibility checks via jest-axe.
- **New shared UI:** Add to the correct atomic tier in `pi-components-catalog/` rather than duplicating inside an app.
- **GraphQL types:** Never hand-edit generated `graphql.ts`; regenerate with `yarn type-generate`.
- **Each package is self-contained:** respect its own lint/test/tsconfig setup when working inside it.
