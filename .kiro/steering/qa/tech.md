---
inclusion: fileMatch
fileMatchPattern: "qa/**"
---

# Tech Stack

Stack-wide QA conventions and commands live in `stacks/qa.md`. This file adds suite-specific
detail.

## Toolchain

- **Playwright** `@playwright/test` 1.52.0, **TypeScript** 5.8, Node.js.
- **dotenv** for local `.env` config.

## Environment Handling

- `ENV` selects the target environment (`uat` default, or `dit`); `playwright.config.ts` maps it
  to `baseURL`, `secureURL`, and `apiBaseURL` for `premierinn.digital`.
- Basic-auth creds come from `AUTH_USERNAME` / `AUTH_PASSWORD`.

## Runtime Notes

- Chromium runs **headed** with `--disable-blink-features=AutomationControlled`; UAT sits behind
  an Akamai WAF that blocks standard headless browsers. Preserve this when adding projects.
- CI-only behaviour keys off `process.env.CI`: `forbidOnly`, 2 retries, single worker.
- Artifacts on failure: trace on first retry, screenshot on failure, video on first retry.
