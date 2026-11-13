# QA Automation Framework — Demo Overview

## Vision

A modern, maintainable Playwright + TypeScript E2E automation suite for Premier Inn's three frontend applications (PI, PIB, CCUI), running on LambdaTest cloud with rich reporting and team notifications.

---

## Framework Architecture

```
qa/
├── config/                      # Runtime configuration
│   ├── environments.ts          # ENV × APP URL matrix (uat/dit × pi/pib/ccui)
│   ├── browsers.ts              # Viewports + local browser projects
│   ├── lambdatest.ts            # Cloud execution (CDP WSS + LT API)
│   └── index.ts                 # Barrel export
│
├── src/
│   ├── components/              # Component Object Model (COM)
│   │   ├── pi/                  # PI-specific reusable UI components
│   │   ├── pib/                 # PIB-specific components
│   │   ├── ccui/                # CCUI-specific components
│   │   └── shared/              # Cross-app components (reserved)
│   │
│   ├── pages/                   # Page Object Model (POM)
│   │   ├── pi/                  # 13 PI page objects
│   │   ├── pib/                 # PIB pages (placeholder)
│   │   └── ccui/                # CCUI pages (placeholder)
│   │
│   ├── fixtures/                # Playwright Fixtures (DI for tests)
│   │   ├── base.fixture.ts      # Shared: dialog handling, LambdaTest auth
│   │   ├── pi.fixture.ts        # PI page objects injected
│   │   ├── pib.fixture.ts       # PIB (placeholder)
│   │   └── ccui.fixture.ts      # CCUI (placeholder)
│   │
│   ├── test-data/               # Typed test data (factories + constants)
│   │   ├── pi/                  # Cards, guests, hotels, booking criteria
│   │   ├── pib/                 # (placeholder)
│   │   └── ccui/                # (placeholder)
│   │
│   ├── api/                     # GraphQL + REST API client library
│   │   ├── graphql/             # Core client, basket, content, availability
│   │   ├── aem/                 # AEM dictionary + cross-validation
│   │   └── ohip/                # Cancellation validation (OPERA)
│   │
│   └── utils/                   # Shared helpers (date, retry)
│
├── tests/                       # Test specs
│   └── regressions/pi/          # PI regression tests
│
├── scripts/                     # Automation scripts
│   ├── run-test.sh              # CLI wrapper (--local, --app, --env, --browser)
│   ├── allure-report.sh         # Generate Allure report with history
│   └── notify-teams.ts          # Teams Adaptive Card notification
│
├── playwright.config.ts         # Slim config (~65 lines) — delegates to config/
├── .env                          # Secrets (gitignored)
├── .env.example                  # Template (committed)
└── package.json                  # npm scripts
```

---

## Key Design Decisions

| Decision | Rationale |
|----------|-----------|
| **POM + COM + Fixtures** | Page objects define screens, components extract reusable UI fragments, fixtures inject them into tests |
| **Layered fixtures** | base → pi/pib/ccui — each app only sees its own page objects |
| **Config separated from secrets** | URLs in TypeScript (committed), credentials in .env (gitignored) |
| **LambdaTest as default** | Cloud-first execution; `--local` flag for local debugging |
| **Allure + Teams** | Rich HTML report + Adaptive Card notification to Teams channel |

---

## Execution Modes

| Command | What it does |
|---------|-------------|
| `./scripts/run-test.sh` | All 4 browsers on LambdaTest → Allure → Teams |
| `./scripts/run-test.sh --local` | Local Chrome → Allure → Teams |
| `./scripts/run-test.sh --app=pib --env=dit` | PIB on DIT, LambdaTest |
| `./scripts/run-test.sh --grep=@smoke` | Smoke tests only |
| `npm run test:debug` | Local, headed, no retries |

### Environment Variables (CLI overrides)

```
APP=pi|pib|ccui        ENV=uat|dit          BROWSER=chrome|firefox|edge|safari
VIEWPORT=desktop|mobile  HEADED=true|false    EXECUTION_ENV=local|lambdatest
```

---

## Reporting Stack

### Allure Report
- Generated after every run
- History preserved for trend graphs (up to 20 runs)
- Environment widget shows app, env, browsers, viewport
- **Suites** mirror folder structure: smoke → pi → Browser: Describe
- **Behaviors** group by feature tag (Datatrans, Universal Login, Smoke, Regression)
- **Categories** classify failures (Timeout, Element Not Found, Assertion, Network, Infrastructure)
- Suite labels patched inline in `allure-report.sh` (no separate file)
- `npm run report:allure` to open

### Teams Notification (Adaptive Card)
- Posts automatically after every run
- Shows: pass/fail/total + success rate, run details, test names, LambdaTest link
- LambdaTest URL fetched via API — links to exact build/test session
- Webhook URL in `.env`

### LambdaTest Dashboard
- All runs visible with build name: `PI-UAT-2026-08-10`
- Each session named: `PI | UAT | Chrome | DESKTOP`
- Video, network logs, console captured per session

---

## Cross-Browser Coverage

| Browser | Engine | Status on LambdaTest |
|---------|--------|---------------------|
| Chrome | Chromium | ✅ Passing |
| Edge | Chromium | ✅ Passing |
| Firefox | Gecko | ⚠️ DLP timeout (auth works, page object stability) |
| Safari | WebKit | ⚠️ DLP DOM timing (auth works, page object stability) |

- WAF bypass (`--disable-blink-features`) for Chrome + Edge
- Auth header injection via `page.route()` for all browsers on LambdaTest
- 5 parallel workers on cloud

---

## Baseline Test (Proof of Concept)

**Full E2E journey covering:**
1. DLP validation (content, TripAdvisor, map/grid, distance)
2. Hotel search via HDP
3. Booking (ancillaries, guest details, PIBA payment, 3DS)
4. Booking confirmation + API cross-validation
5. Amendment (Double → Family room)
6. Cancellation + status verification

**Runs in ~4 min on LambdaTest Chrome.**

---

## Test Structure & Tagging

```
tests/
├── smoke/                    # Quick sanity checks (@smoke)
│   ├── pi/                   # PI smoke tests
│   ├── pib/                  # PIB smoke tests
│   └── ccui/                 # CCUI smoke tests
├── regressions/              # Full regression pack (@regression)
│   ├── pi/                   # PI regression + baseline E2E
│   ├── pib/                  # PIB regression
│   └── ccui/                 # CCUI regression
└── features/                 # Feature-specific tests (@feature-tag)
    ├── book-pay/             # Datatrans (@datatrans), Promo Code (@promocode)
    ├── identity/             # Universal Login (@universalLogin), TMC Users (@tmcUsers)
    ├── discover-search/      # (placeholder)
    └── arrive-stay-leave/    # (placeholder)
```

**Run by tag:**
```bash
./scripts/run-test.sh --grep=@smoke           # Smoke pack only
./scripts/run-test.sh --grep=@regression      # Regression pack only
./scripts/run-test.sh --grep=@datatrans       # Specific feature
./scripts/run-test.sh --grep=@universalLogin  # Specific feature
```

---

## What's Next

- Finalize API structure
- Create smoke packs with new structure
- Push test scenarios and test results to Lambda test
- CI integration (GitHub Actions workflow)
- Implementation for mobile apps and Distribution based on POCs

