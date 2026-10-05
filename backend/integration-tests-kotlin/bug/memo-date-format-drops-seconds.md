# Memo dates are formatted with HH:mm:SS, dropping seconds and emitting milliseconds

**Service:** ohip-adapter-service
**Endpoints:** `GET /ohip/v1/reservations/memos`, `POST /ohip/v1/reservations/memos`
**Flow docs:** `backend/integration-tests-kotlin/flows/ohip-adapter-service/GetReservationMemos.md`,
`backend/integration-tests-kotlin/flows/ohip-adapter-service/AddReservationMemos.md`
**Scenarios:** `ohip.GetReservationMemosSpec`, `ohip.AddReservationMemosSpec` — all enabled;
the stub-derived audit stamps use whole minutes (zero seconds, zero millis), so the two
patterns produce identical output on test data and no assertion is affected.

## Chain

`MemosOhipMapper.formatDate` (line ~160) formats `createdOn`/`modifiedOn` with
`COMMENT_DATE_PATTERN = "yyyy-MM-dd HH:mm:SS"` (line 41) via commons-lang
`DateFormatUtils.format`.

## Expected

Memo dates rendered as `yyyy-MM-dd HH:mm:ss` (lowercase `ss` = seconds), matching the
service's own `JacksonConfig.TIMESTAMP_DATE_FORMAT` and the shape Opera sends.

## Actual

In commons-lang patterns `S` is *millisecond*, so `SS` renders a two-digit millisecond
field where seconds belong. A comment modified at `09:01:45.000` is returned as
`2026-09-01 09:01:00` — the `45` seconds are silently discarded and `00` is milliseconds.
Two comments modified within the same minute can render identical `modifiedOn` values,
making the `modifiedOn`-descending memo sort nondeterministic between them, and any
consumer parsing the value as seconds reads a wrong instant.

## Reproduction

Not directly provable through the journey suite: the default stub's derived audit stamps
are whole minutes, so both patterns emit the same text. Provable with a custom
get-reservation override whose `lastModifyDateTime` carries non-zero seconds (e.g.
`2026-09-01 09:01:45`): the memo response returns `09:01:00` instead of `09:01:45`.

## Fix hint

Change `COMMENT_DATE_PATTERN` to `yyyy-MM-dd HH:mm:ss`.

## Note

Found via GitHub PR #227 review: Copilot flagged the flow docs' `HH:mm:SS` as a doc typo;
verification showed the docs were faithfully transcribing this service constant. The flow
docs now state the pattern is literal and link here.
