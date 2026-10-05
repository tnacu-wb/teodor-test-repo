---
inclusion: fileMatch
fileMatchPattern: "qa/**"
---

# QA Stack Conventions

Applies to everything under `qa/` — the Playwright + TypeScript end-to-end suite.

## Core

- **Runner:** Playwright (`@playwright/test` 1.52.0).
- **Language:** TypeScript 5.8, Node.js types `@types/node`.
- **Config:** `dotenv` loads a `.env` file; environment selected via `ENV` (default `uat`,
  also `dit`), which picks base/secure/API URLs in `playwright.config.ts`.
- Basic auth via `AUTH_USERNAME` / `AUTH_PASSWORD` env vars.

## Commands

```bash
npm test            # npx playwright test (headless)
npm run test:headed # headed run
npm run test:ui     # Playwright UI mode
npm run report      # show HTML report
```

## Conventions

- **Page Object Model.** Page objects live in `src/pages/{pi,ccui,pib}/` (one file per
  screen, e.g. `home.page.ts`, `payment.page.ts`), re-exported from the app's `index.ts`
  barrel (create it if missing — currently only `pi/` has one; `ccui/` and `pib/` are
  placeholders).
- **Fixtures** extend the base test in `src/fixtures/base.fixture.ts`.
- **Test data** as typed constants in `src/test-data/{pi,ccui,pib}/` (`cards.ts`,
  `hotels.ts`, `guestData.ts`) — never hard-code data in tests.
- **Specs** live under `tests/regressions/{pi,ccui,pib}/`, named `*.spec.ts`.
- Chromium runs **headed** by default with `--disable-blink-features=AutomationControlled`
  because UAT sits behind an Akamai WAF that blocks headless browsers. Keep this when adding
  browser projects.
- Retries and single worker are enabled only in CI (`process.env.CI`).
