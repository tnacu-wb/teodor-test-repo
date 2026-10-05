---
inclusion: fileMatch
fileMatchPattern: "frontend/pi-front-end-applications/**"
---

# Tech Stack

## Core

- **Language:** TypeScript 5.5 (strict mode). Compile target `es5`, module `esnext`, `moduleResolution: bundler`.
- **Runtime:** Node.js `>=22.22.2` (see `.nvmrc`, pinned to 22.22.2). `engine-strict` is on.
- **Framework:** Next.js 15.5 (Pages Router — `src/pages/`).
- **UI:** React 19, Chakra UI 2.8, Emotion, Framer Motion. Some shared components use Radix UI + shadcn-ui and Tailwind-style utilities (`clsx`, `class-variance-authority`).
- **Data:** GraphQL (`graphql-request`, `graphql-ws`), TanStack React Query for client data fetching, Immer/`use-immer` for state.
- **Forms & validation:** React Hook Form, Yup, and Zod.
- **Other:** Auth0 (`@auth0/nextjs-auth0`), Unleash feature flags, ioredis, Firebase, Winston logging, i18next/next-i18next for localization.

## Monorepo Tooling

- **Package manager:** Yarn (v1, classic) with workspaces. Do not use npm.
- **Orchestration:** Lerna (run/scope commands) on top of Nx (task caching for build/test/lint/type-check).
- **Internal packages:** Published under the `@whitbread-eos/*` scope to GitHub Packages registry (`npm.pkg.github.com`).
- **Component builds:** Atomic-design packages are compiled with Babel (+ `tsc` for declarations); apps build with Next.js.

## Code Style

- **Prettier:** single quotes, 2-space indent, semicolons, `printWidth` 100, `trailingComma: es5`. Imports are sorted (`~`-aliased first, then relative) with separation between groups.
- **ESLint:** TypeScript + React + react-hooks + import + prettier configs. `prettier/prettier` and `@typescript-eslint/no-unused-vars` are errors; `no-console` warns. Auto-formatting and `eslint --fix` run on staged files via Husky + lint-staged.
- **Path aliases:** Use `~`-prefixed imports inside apps (e.g. `~components/*`, `~hooks/*`, `~utils/*`, `~store/*`) per each app's `tsconfig.json` `paths`.
- **Commits:** Conventional Commits enforced by commitlint + Husky `commit-msg`. Use `yarn commit` (Commitizen) for guided commits.

## Common Commands

Run from the repo root unless noted. Lerna scopes target individual packages.

### Dev servers (run manually in your terminal — these are long-running)
- `yarn start:pi` — premier-inn dev server
- `yarn start:ccui` — ccui dev server
- `yarn start:bb` — business-booker dev server

### Build
- `yarn build` — build all packages
- `yarn build:pi` / `yarn build:ccui` / `yarn build:bb` — build a single app
- `yarn build:atomic:components` — build all `@whitbread-eos/*` shared packages
- `yarn build:all` — clean install + type generation + component build (full from-scratch build)

### Test
- `yarn test` — run all tests (Jest)
- `yarn test:coverage` — tests with coverage
- `yarn test:ci` — CI mode (silent, coverage, bail)
- Within a package: `yarn test:watch` for watch mode

### Quality
- `yarn lint` / `yarn lint:fix` — lint all packages
- `yarn type-check` — TypeScript type checking across packages
- `yarn check-integrity:all` — type-check + lint:fix + test + build (full validation)

### GraphQL types
- `yarn type-generate --local` — generate TS types from the GraphQL schema (uses `.env.local`), output to `pi-components-catalog/api/src/types/graphql.ts`
- `yarn type-generate --targetEnv=<env>` — generate against a deployed environment schema

### E2E
- `yarn automation` / `yarn automation:ui` / `yarn automation:report` — Playwright end-to-end tests
