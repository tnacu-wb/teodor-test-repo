---
inclusion: fileMatch
fileMatchPattern: "frontend/**"
---

# Frontend Stack Conventions

Applies to every module under `frontend/`. App-specific detail lives in
`frontend/<app>/{product,structure,tech}.md`.

## Core

- **Language:** TypeScript 5.5 (strict mode). Target `es5`, module `esnext`,
  `moduleResolution: bundler`.
- **Runtime:** Node.js `>=22.22.2` (pinned in `.nvmrc`; `engine-strict` is on).
- **Framework:** Next.js 15.5 (Pages Router — `src/pages/`).
- **UI:** React 19, Chakra UI 2.8, Emotion, Framer Motion; some shared components use Radix UI
  + shadcn-ui and Tailwind-style utilities (`clsx`, `class-variance-authority`).
- **Data:** GraphQL (`graphql-request`, `graphql-ws`), TanStack React Query, Immer/`use-immer`.
- **Forms & validation:** React Hook Form, Yup, Zod.
- **Other:** Auth0 (`@auth0/nextjs-auth0`), Unleash feature flags, ioredis, Firebase, Winston,
  i18next / next-i18next.

## Monorepo Tooling

- **Package manager:** Yarn v1 (classic) with workspaces. **Do not use npm.**
- **Orchestration:** Lerna (run/scope commands) over Nx (task caching for build/test/lint/type-check).
- **Internal packages:** published under `@whitbread-eos/*` to GitHub Packages
  (`npm.pkg.github.com`).
- **Component builds:** atomic-design packages compile with Babel (+ `tsc` for declarations);
  apps build with Next.js.

## Code Style

- **Prettier:** single quotes, 2-space indent, semicolons, `printWidth` 100,
  `trailingComma: es5`. Imports sorted (`~`-aliased first, then relative), grouped.
- **ESLint:** TypeScript + React + react-hooks + import + prettier. `prettier/prettier` and
  `@typescript-eslint/no-unused-vars` are errors; `no-console` warns. `eslint --fix` runs on
  staged files via Husky + lint-staged.
- **Path aliases:** use `~`-prefixed imports inside apps (`~components/*`, `~hooks/*`, etc.);
  import shared code from `@whitbread-eos/*`, not deep relative paths.
- **Commits:** Conventional Commits enforced by commitlint + Husky. Use `yarn commit` (Commitizen).

## Common Commands

Run from the frontend repo root. Dev servers are long-running — start them manually.

```bash
# Dev servers (manual)
yarn start:pi | yarn start:ccui | yarn start:bb

# Build
yarn build                      # all packages
yarn build:pi | :ccui | :bb     # single app
yarn build:atomic:components    # shared @whitbread-eos/* packages
yarn build:all                  # clean install + type gen + component build

# Test
yarn test | yarn test:coverage | yarn test:ci

# Quality
yarn lint | yarn lint:fix | yarn type-check
yarn check-integrity:all        # type-check + lint:fix + test + build

# GraphQL types (never hand-edit generated graphql.ts)
yarn type-generate --local
yarn type-generate --targetEnv=<env>
```
