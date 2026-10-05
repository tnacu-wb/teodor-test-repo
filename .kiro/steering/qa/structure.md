---
inclusion: fileMatch
fileMatchPattern: "qa/**"
---

# Project Structure

Playwright + TypeScript suite using the Page Object Model.

```
qa/
├── src/
│   ├── api/                # Shared API clients (GraphQL, OHIP, AEM); barrel via index.ts
│   ├── components/         # Shared + per-app UI components ({pi,ccui,pib,shared}/)
│   ├── fixtures/           # base.fixture.ts — extended Playwright test fixtures
│   ├── pages/{pi,ccui,pib}/# Page objects, one per screen; barrel exported via index.ts
│   ├── test-data/{pi,ccui,pib}/ # Typed test data (cards, hotels, guestData)
│   └── utils/              # Shared utilities
├── tests/regressions/{pi,ccui,pib}/ # Regression specs (*.spec.ts)
├── playwright.config.ts    # Env-driven config (ENV=uat|dit)
├── package.json
└── tsconfig.json
```

## Conventions

- **One page object per screen** in `src/pages/{pi,ccui,pib}/` (e.g. `home.page.ts`,
  `hotelDetails.page.ts`, `payment.page.ts`); export new ones from the app's `index.ts`
  barrel (create it if missing — currently only `pi/` has one; `ccui/` and `pib/` are
  placeholders).
- **Test data** belongs in `src/test-data/{pi,ccui,pib}/` as typed constants — never inline
  literals in specs.
- **Shared setup** (auth, page fixtures) goes through `src/fixtures/base.fixture.ts`; import the
  extended `test` from there rather than `@playwright/test` directly.
- **Specs** live in `tests/regressions/{pi,ccui,pib}/`, named `*.spec.ts`.
