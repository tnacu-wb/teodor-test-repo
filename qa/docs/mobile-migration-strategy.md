# Mobile App Automation Migration Strategy — AppsAutomation-WebDriverIO into the Monorepo

> How the existing native-mobile automation framework (`AppsAutomation-WebDriverIO`) moves
> into the monorepo `qa/` stack. Defines the core decision (§1 — lift-and-shift WebdriverIO,
> do **not** rewrite into Playwright), the target structure (§2), a phased low-risk migration
> (§3), what gets shared with the Playwright web suite (§4), what gets dropped (§5), and the
> open risks (§6). Companion to `regression-pack-migration-strategy.md`, which covers the
> separate WebdriverIO-web → Playwright migration.

---

## 1. Core Decision

### 1.1 The two things called "migration" here

There are two independent migrations, and they must not be confused:

- **Web:** old WebdriverIO **web** pack → **Playwright** (`qa/`). Covered by
  `regression-pack-migration-strategy.md`. A rewrite is justified there because the target
  runner (Playwright) genuinely drives web browsers.
- **Mobile (this doc):** existing WebdriverIO **native app** framework
  (`AppsAutomation-WebDriverIO`) → into the monorepo. This is a **lift-and-shift**, not a
  rewrite.

### 1.2 Decision: lift-and-shift WebdriverIO, do NOT rewrite into Playwright

Playwright cannot drive native `.apk` / `.ipa` apps. The only "Playwright + Appium" pattern
uses the `webdriverio` client library under the hood to talk to Appium — so a rewrite would
keep the exact same Appium driver layer and WebdriverIO element syntax (`$()`, `.click()`,
`.setValue()`), while swapping only the test runner (Mocha → Playwright) and the assertion
library (`chai` → `expect-webdriverio`). That is a large cost for a cosmetic benefit.

The existing framework is mature and already works:

| Attribute            | Detail                                                                 |
|----------------------|------------------------------------------------------------------------|
| Runner / language    | WebdriverIO 9 + Appium 3 + Mocha, JavaScript (Babel, ESM)              |
| Active specs         | ~95 (`tests/specs/`, excluding `depricated/`)                          |
| Deprecated specs     | 13 (`tests/specs/depricated/`)                                         |
| Screen objects (POM) | 43 (`tests/screenobjects/`)                                            |
| Platforms            | Android (UiAutomator2) + iOS (XCUITest), real devices                  |
| Apps covered         | Premier Inn (leisure, guest + registered) and Premier Inn Business     |
| Cross-platform model | External `locator.json` (ios/android per screen) + `labels.json` (en/de) resolved by `LabelProvider` / `BaseScreen.getElement()` |
| API layer            | GraphQL (`graphql-request`) + `.graphql` fixtures + REST (`superagent`) for booking-flow setup |
| Cloud                | BrowserStack (original) with an **in-progress LambdaTest migration** already present |
| Reporting            | Allure + JUnit + Teams; tag-based suites (`@ios`, `@android`, `@ios_smoke`, `@regression`, …) |

**Conclusion:** finish the LambdaTest cutover and move the framework into the monorepo as-is.
Keep **Playwright for web, WebdriverIO for native** — two runners, cleanly separated, which is
the industry-normal arrangement.

---

## 2. Target Structure

The mobile suite lands as a sibling workspace to the Playwright web suite under a
workspaces-root `qa/` (the "Option 4" shape):

```
qa/                              # npm-workspaces root
├── shared/                      # @premierinn/qa-shared (runner-agnostic TS/JS)
│   ├── test-data/               # users, cards, hotels        ← web + mobile
│   ├── api/                     # GraphQL/REST, transport-decoupled (HttpTransport)
│   ├── lambdatest/              # build/session naming, TM @TC-xxx linking
│   └── notify/                  # single Teams notifier
├── web/                         # Playwright (current qa/ contents, moved in) — unchanged
└── mobile/                      # ← AppsAutomation-WebDriverIO lifted in
    ├── config/                  # wdio.*.lt.conf.js kept; BrowserStack confs dropped (§5)
    ├── tests/
    │   ├── specs/               # leisure/, premierInnBusiness/ (depricated/ dropped)
    │   ├── api/                 # GraphQL/REST calls (candidate to share via shared/, §4)
    │   ├── driver/              # locator.json, labels.json, LabelProvider, GenericScreen
    │   ├── fixtures/            # *.graphql query/mutation fixtures
    │   └── constants/
    ├── screenobjects/           # 43 POM classes — preserved untouched
    ├── utilities/               # teamsReport.js (repoint to shared notifier, §4)
    └── package.json             # wdio, appium; imports @premierinn/qa-shared
```

If the full workspaces reorg is deferred, phase 1 can instead land the framework as a
standalone `qa-mobile/` sibling and be folded into `qa/mobile/` later — the phasing in §3
holds either way.

---

## 3. Phased Migration

Each phase is independently shippable and leaves the suite runnable.

### Phase 1 — Lift-and-shift, prove a green run
- Copy the framework into `qa/mobile/` **as-is** (JavaScript, WDIO, Mocha — no rewrite).
- Wire its `.env` and LambdaTest credentials into the monorepo's config conventions.
- Success criterion: one **smoke** run (e.g. `login.spec.js`) passes on a LambdaTest real
  device from inside the monorepo, Android and iOS.

### Phase 2 — Finish the LambdaTest cutover and trim
- Confirm the `wdio.android.lt.conf.js` / `wdio.ios.lt.conf.js` paths run reliably.
- Remove BrowserStack config and dependencies once LT is confirmed (§5).
- Delete `tests/specs/depricated/`.

### Phase 3 — Unify LambdaTest conventions with the web suite
- Align mobile build/session naming and `@TC-xxx` Test Manager linking with the web suite's
  `qa/config/lambdatest` logic so both suites feed the **same LT account, TM project and
  dashboard**. This is the main point of genuine convergence between web and mobile.

### Phase 4 — Share cross-cutting code (§4)
- Repoint mobile Teams reporting at the shared `notify-teams`.
- Share `test-data` and the GraphQL/REST API layer via `shared/` using the `HttpTransport`
  decoupling (mobile's `graphql-request` + `superagent` are replaced by the shared client
  with a `fetch` adapter).

### Phase 5 — TypeScript (optional, later)
- Convert incrementally only if the team wants type-safety across shared code. Not required
  for the framework to live in the monorepo — WDIO runs JavaScript fine.

---

## 4. What Gets Shared With the Web Suite

Only runner-agnostic code is shareable. The runners themselves (Playwright vs WDIO/Appium)
stay separate.

| Asset                        | Shareable? | Notes                                                             |
|------------------------------|:----------:|-------------------------------------------------------------------|
| Test data (users/cards/hotels)| ✅ fully    | Plain data; unify web + mobile constants in `shared/test-data`    |
| GraphQL/REST API logic       | ✅ logic    | Decouple transport (`HttpTransport` interface + fetch adapter); replaces mobile's `graphql-request`/`superagent` |
| `.graphql` query/mutation fixtures | ✅     | Same booking-flow queries as the web suite — dedupe into `shared` |
| LambdaTest naming + TM linking| ✅          | Single source in `shared/lambdatest`, consumed by both suites     |
| Teams notifier               | ✅          | Replace `utilities/teamsReport.js` with the shared notifier       |
| Screen objects / locators / labels | ❌     | Native-only (Appium locators, platform/locale resolution) — stay in `qa/mobile` |
| WDIO config / hooks          | ❌          | Runner-specific                                                   |

The API sharing depends on the same transport-decoupling refactor described for the web
suite: the client depends on a small `HttpTransport` interface, with a Playwright adapter for
web and a `fetch` adapter for mobile.

---

## 5. What Gets Dropped

| Item                                   | Why                                             |
|----------------------------------------|-------------------------------------------------|
| `wdio.*.bs.conf.js` (BrowserStack)     | LambdaTest is the standardised cloud            |
| `browserstack-local`, `@wdio/browserstack-service` | Unused after BrowserStack removal    |
| `tests/specs/depricated/` (13 specs)   | Explicitly deprecated                           |
| Duplicated Teams/report code           | Superseded by the shared notifier (§4)          |

Drop BrowserStack only **after** the LambdaTest run is confirmed green (Phase 2), to avoid
losing a working fallback mid-migration.

---

## 6. Risks and Open Items

- **App binaries / IDs.** Mobile needs `.apk` / `.ipa` uploaded to LambdaTest (the
  `app: lt://…` id). CI needs a per-build app-upload step — unlike web's URL-based testing.
- **Language/tooling mismatch.** Web is TS/Playwright; mobile is JS/WDIO/Mocha. `shared/`
  must be consumable by both — compile it to JS (or ship `.d.ts`) so the JS mobile workspace
  and TS web workspace both import cleanly.
- **OCR dependency.** `wdio-ocr-service` + tesseract is heavy and native; confirm CI runners
  support it or gate the OCR specs.
- **In-flight LT migration.** Finish the BrowserStack → LambdaTest cutover as part of Phase 2
  so the team isn't maintaining BrowserStack + LambdaTest + the monorepo move at once.
- **App-restart recovery hook.** The `afterTest` hook hard-codes app IDs
  (`com.whitbread.pi`, `com.whitbread.premierinn.stage`) — parameterise per environment
  during the move.
- **`before` hook coupling.** The global `before` runs privacy/policy acceptance and
  environment switching for every spec; validate it still holds under the monorepo run.

---

## 7. Recommendation Summary

Migrate, don't rewrite. Lift the WebdriverIO native suite into `qa/mobile/` beside the
Playwright web suite, finish the LambdaTest cutover, then progressively share test-data, the
API layer, LambdaTest naming and Teams-notify through `shared/`. Keep Playwright for web and
WebdriverIO for native. A Playwright rewrite would cost a full re-port of ~95 specs and 43
screen objects to end up driving Appium through `webdriverio` anyway.

---

## 8. Framework Improvements

A review of the current `AppsAutomation-WebDriverIO` internals. The framework is
production-grade — clean POM inheritance (`BaseScreen` → screen objects), a solid generic
driver layer (`GenericScreen`), and a smart platform+locale abstraction (`locator.json` +
`labels.json` + `LabelProvider`). The items below are maintainability, correctness and
monorepo-fit improvements, not a rescue. Items marked **(do with the move)** are best done as
part of the migration so the framework isn't restructured twice.

### 8.1 High impact

**8.1.1 `locator.json` is re-read from disk on every element access.**
`BaseScreen.getElement()` calls `readJsonFile()` (`fs.readFileSync` + `JSON.parse` of the
whole `locator.json`) on **every** getter access. A single screen with 10 interactions
re-parses the entire locator file 10+ times; a booking journey does it thousands of times.
`LabelProvider` already does this correctly — it reads once in its constructor.
*Fix:* load and cache `locator.json` once at module level, mirroring `LabelProvider`.
*Effort:* Low. *Payoff:* High (performance).

**8.1.2 Config duplication and a divergent specs glob across wdio confs.**
`wdio.android.lt.conf.js` and `wdio.ios.lt.conf.js` repeat LT credentials, host/port/protocol/
path, services, parallel logic and reporters, and BrowserStack confs add a third copy. They
also diverge: Android uses `config.specs = ['./tests/specs/**/*.spec.js']` while iOS uses
`['./tests/specs/**/*.js']` — the iOS glob will try to run non-spec `.js` files (latent bug).
*Fix:* one shared LT config holding the common settings; platform files override only
capabilities. Align the specs glob to `**/*.spec.js` everywhere.
*Effort:* Low. *Payoff:* High.

**8.1.3 Secrets / test-data / config are fused in the runner config.**
`wdio.shared.conf.js` hard-codes ~40 `userDetails` accounts, hotel names and card numbers,
all sourced from env. This mixes secrets, data and runner config. In CI (no user-level
config) it is brittle, and credentials / card numbers in env are sensitive.
*Fix:* split the three concerns — secrets via CI secret store / uncommitted `.env`;
non-secret data (hotel names, card *types*) into typed `test-data` modules aligned with the
web suite's `src/test-data`; keep wdio config lean. Sets up the `shared/test-data` reuse in §4.
*Effort:* Med. *Payoff:* High (monorepo + security). **(do with the move)**

### 8.2 Medium impact

**8.2.1 Implicit globals everywhere.** Screen objects reference `driver.isIOS`,
`browser.options`, `global.platform` directly — untyped, hard to unit-test, coupled to
runtime globals. *Fix (incremental):* wrap in a thin helper (`platform.isIOS()`,
`env.get('PI_HOTEL')`). Pays off most under TypeScript.

**8.2.2 Inconsistent soft vs hard waits in `GenericScreen`.** `clickOnElement` uses
`waitForElementToBeEnabledSoft` (swallows errors, continues), while `sendKeys` uses the hard
`waitForElementToBeDisplayed` (throws). A click on a missing element silently proceeds to
`.click()` and fails with a cryptic Appium error instead of a clear timeout message.
*Fix:* hard waits by default (fail fast with the existing `timeoutMsg`); explicit
`clickSoft` / `isDisplayedSoft` variants where the element is genuinely optional.

**8.2.3 Hard-coded app IDs and inlined setup in hooks.** `afterTest` hard-codes
`com.whitbread.pi` / `com.whitbread.premierinn.stage`; the global `before` inlines the whole
privacy/policy/cookie flow and env switch for every spec; version checks use magic numbers
(`>= 15 || == 13`). *Fix:* app IDs to env/config; extract the `before` flow into a named,
per-suite-overridable setup helper; name the version constants. **(do with the move)**

**8.2.4 Duplicated and partly broken timeout getters.** `GenericScreen.fifteenSecondsTimeout`
reads `browser.options.fifteenSecondsTimeout`, which is not defined in `wdio.shared.conf.js`
(only `fiveSeconds`, `tenSeconds`, `twentySeconds`, `thirtySeconds`, `fourtySeconds` exist),
so it returns `undefined` — and `LoginScreen.enterLoginDetailsViaBooking` passes it into
`clickOnElement`. *Fix:* one canonical timeouts map, referenced consistently; add `15s` if
intended; delete dead getters.

### 8.3 Lower impact — consistency

- `policyScreen.js` is lowercase; the other 42 screen objects are PascalCase.
- `MyBookingScreen.js` vs `MyBookingsScreen.js` — near-identical, easy to mis-import.
- `depricated/` is a typo for `deprecated/` (being deleted anyway, §5).
- `CommonScreen.elementWithContainsStaticText` returns `elementWithContainsText`
  (copy-paste bug).
- `enterText` / `updateText` use nested ternaries with duplicated branches — refactor into
  explicit `if` blocks for readability.
- Mixed API/util libraries (`chai`, `superagent`, `graphql-request`, `fuse.js`, `xml2js`,
  `node-tesseract-ocr`) — the API layer overlaps the web suite and is the prime candidate to
  consolidate into `shared/` (§4).

### 8.4 Structural improvements to apply during the move

1. **Promote the engine to a `support/` (or `core/`) layer.** Group `GenericScreen`,
   `BaseScreen`, `LabelProvider` and the locator/label data — currently mixed under `driver/`
   — into a clear support layer.
2. **Split data from code.** `locator.json` (selectors) and `labels.json` (copy) are data;
   keep them under `resources/` / `test-data/`, separate from the code that reads them.
3. **Group the 43 screen objects by domain** (`booking/`, `ciol/`, `account/`, `search/`,
   `amends/`) instead of one flat folder — mirrors the squad/feature structure and clarifies
   ownership.
4. **One env-driven config entry.** Replace separate `android`/`ios` LT confs with a single
   config reading `PLATFORM=android|ios`, matching the web suite's env-driven model
   (`ENV`, `BROWSER`, `VIEWPORT`).

### 8.5 Priority order

| Priority | Item | Effort | Payoff |
|---------:|------|:------:|:------:|
| 1 | Cache `locator.json` (§8.1.1) | Low | High |
| 2 | De-duplicate wdio confs + fix iOS specs glob (§8.1.2) | Low | High |
| 3 | Fix broken/duplicated timeout getters (§8.2.4) | Low | Med |
| 4 | Separate secrets / test-data / config (§8.1.3) | Med | High |
| 5 | Make wait soft/hard behaviour explicit (§8.2.2) | Med | Med |
| 6 | Extract hard-coded app IDs + `before` flow (§8.2.3) | Low | Med |
| 7 | Naming/typo cleanup (§8.3) | Low | Low |
| 8 | Domain-group screen objects + `support/` layer (§8.4) | Med | Med |

Items 1–3 are quick correctness/performance wins worth doing regardless of the migration.
Items 4 and 8 are best done **as part of** the move so the framework isn't restructured twice.
