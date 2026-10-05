# Authoring pages in docs/kotlin-for-java-devs

Guidelines for agents adding or editing pages in this folder. The reference
implementation is `service-api-client.html` — when these guidelines and that file
disagree on a value, the file wins; fix the guideline.

## What this folder is

Human-facing HTML explainers for the Java developers on this team. Each page teaches one
framework concept from this module and the Kotlin features it uses, assuming no Kotlin
knowledge. Pages are opened directly from disk (`file://`), often offline.

## Hard constraints

1. One self-contained `.html` file per topic, kebab-case name (`service-api-client.html`).
   No external requests of any kind: no CDN scripts, webfonts, or remote images. System
   font stacks only.
2. Real code only. Every Kotlin example is taken from (or trivially reduced from) this
   repo's actual source, and every Java comparison compiles in the reader's head. When
   the code the page quotes changes, the page is stale — say so in the page footer which
   contract the page tracks.
3. Interactivity is vanilla JS, small enough to read in one screen. Prefer native
   elements (`<details>` for expanders) over scripted ones. Keyboard focus must be
   visible; wrap animation opt-outs in `prefers-reduced-motion`.

## Visual system

The look is borrowed from Codecademy's design system (Gamut). Copy the `:root` /
`@media (prefers-color-scheme: dark)` / `[data-theme="dark"]` token blocks from
`service-api-client.html` verbatim into a new page; do not re-derive colors.

Roles, which matter more than the hex values:

- **Ground**: warm beige page (`--paper`), pure white cards (`--surface`), near-black
  navy text (`--ink`). All type is the system sans stack; only code is monospace.
- **Cards**: white, 1px navy border (`--border`), 8px radius. Soft peach hairlines
  (`--line`) are only for table rows and the TOC — never card outlines.
- **Blue (`--accent`) is rationed.** It appears as a filled button (selected tab) and as
  link color. Nothing else — no blue eyebrows, section numbers, chips, or bars. If a
  new component wants emphasis, it gets navy or yellow, not blue.
- **Yellow (`--java-soft`) is the highlight family**: pill-shaped chips with navy text
  (keyword chips, the Java-side tab), callout left bars, soft pale-yellow tints
  (`--accent-soft`) for emphasized diagram rows.
- **Code blocks** are the one large navy surface (`--code-bg`), with yellow keywords,
  mint strings, periwinkle types.
- **Theming**: the token blocks implement the three-state pattern (bare `:root` = full
  light palette; dark redefined under `@media` guarded with `:not([data-theme="light"])`;
  redefined again under `[data-theme="dark"]`). Style components only through tokens.
  Never give a color its only definition inside a media or `[data-theme]` block, and
  keep `body { background: var(--paper) }`.

## Page structure

Follow the skeleton of the reference page:

1. Masthead: uppercase eyebrow (`integration-tests-kotlin · docs for Java developers`),
   page name as a short noun phrase, 2–3 sentence standfirst.
2. Sticky left TOC (hidden under 58rem), numbered sections, ~46rem prose column.
3. "Why it exists" before "how to use it" before "language features".
4. Kotlin feature cards: keyword chip + "Java nearest thing" one-liner up front, prose,
   then a Kotlin ⇄ Java tab panel, then an optional `<details>` "what breaks without it?".
5. End with a cheat-sheet table and a callout pointing at the real source files.

Reuse the existing CSS classes (`.panel`, `.tab`, `.frame`, `.callout`,
`details.without`, `.kw-chip`) rather than inventing parallel ones.

### Class pages

A page about a class must include a complete class map before the implementation
walkthrough.

1. List every method declared by the class. Include public, internal, and private
   methods.
2. Use the exact source method name in each map row.
3. Show the input and the output or effect of each method.
4. Add a link from each map row to one dedicated explanation below the map.
5. Give each method its own heading. Use the exact method name in that heading.
6. Do not use one lifecycle or feature section as a substitute for the individual
   method sections.
7. Show the real Kotlin method or the complete relevant extract in each method
   section.
8. Explain the method purpose, control flow, result, side effects, and failure
   behavior.
9. Explain null and cancellation behavior when the method has either behavior.
10. Add a Java comparison or a realistic call example for each method.
11. Explain private methods with the same care as caller-facing methods.
12. List local functions under their owning method. Explain each local function that
    controls behavior or error handling.

Keep constructors, properties, file-level functions, and extension functions separate
from the class method list. Explain them in their own sections when they affect the
class contract. A constructor is not a method. A local function is not a class method.
An extension function declared outside the class is not a class method.

## Writing rules

- Audience: experienced Java developers, zero Kotlin. Every Kotlin construct gets a Java
  anchor: the nearest Java concept, or an honest "no equivalent".
- Every page is self-contained. Do not link to or depend on other pages in this folder.
  Every construct the page's quoted code uses gets its anchor on the page itself — a
  feature card for the load-bearing ones, a cheat-sheet row for the basics. Constructs
  identical in Java (`try`/`catch`/`finally`, `class`, `return`) need no anchor.
- Explain *why the code needs the feature*, not just what it is. The strongest device is
  the without-version: show the code the feature deletes.
- Short, direct sentences. Active voice. One term per concept across the whole folder
  (e.g. always "typed client", never a synonym).
- Titles are names, not summaries: `Inside ServiceApiClient`, not
  "ServiceApiClient — how our clients work".

## Before finishing

1. Parse-check the HTML (unclosed/mismatched tags) — the reference page was checked with
   a small Python `HTMLParser` script; anything equivalent is fine.
2. Grep your stylesheet for colors defined only inside a media or `[data-theme]` block.
3. Open the page mentally in both themes: every foreground color must sit on a token
   surface from the same theme.
4. Verify every repo path and class name the page cites still exists.
5. For a class page, compare the source declarations with the class map. Verify that
   every class method has one map row, one working link, and one dedicated explanation.
