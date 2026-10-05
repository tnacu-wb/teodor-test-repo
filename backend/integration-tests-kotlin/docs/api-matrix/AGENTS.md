# API Matrix Rules

Apply these rules to each `*_API.md` file in this folder.

## Table Shape

- Keep the matrix file table-only.
- Use one row for each public service endpoint.
- Format each API as `METHOD /effective/path` in inline code.
- Add an adjacent API and stub column pair for each external dependency, such as `CDH API` and `CDH Default STUB ID`.
- Keep an existing dependency column pair when its cells are empty.
- Add a new `<Dependency> API` and `<Dependency> Default STUB ID` pair when the service gains another external dependency.
- Put multiple calls to the same dependency on separate lines with `<br>`.
- Put each stub on the same line position as its external API.
- Keep `testClass` and `flow doc` as the final two columns.

## Dependency and Stub Mapping

- Trace the complete runtime path from the public controller to each external business API.
- Include direct calls and calls made through an internal service.
- Include calls that occur only on a request branch.
- Exclude JWKS, OAuth token, actuator, tracing, and cache infrastructure calls.
- Read the implementation for each call. Do not infer calls from a flow document or a class name.
- Read `stubs/DefaultStubs.kt` and the selected stub builder before recording a default stub ID.
- Record the exact stub ID string.
- Leave the matching stub position empty when an external API has no default stub.
- Preserve `<br>` positions when one API in a multi-line dependency cell has no default stub.

## Coverage Columns

- Inventory the journey specs under `src/integrationTest/.../journeys/<service-key>/`.
- Read the typed service client and map each client method to its exact HTTP method and path.
- Link `testClass` only when a journey spec in that service journey folder calls the typed client method for the endpoint.
- Use the same journey spec link in multiple rows when that spec directly tests multiple endpoints.
- Count direct service journey coverage only. A journey spec for another service does not populate `testClass`.
- Leave `testClass` empty when no direct service journey spec calls the endpoint.
- A populated `testClass` cell means that a journey spec directly calls the endpoint. It does not mean that every dependency call or branch has coverage.
- Link `flow doc` to the endpoint flow document under `flows/<service>/`.
- Leave `flow doc` empty when no flow document exists for the endpoint.
- Put rows with both `testClass` and `flow doc` values first.
- Keep any order within the complete and incomplete groups.
- Renumber the `ID` column from `1` after every reorder.

## Links

- Use relative links from `docs/api-matrix/`.
- Use `../../src/integrationTest/...` for journey test links.
- Use `../../flows/<service>/...` for flow document links.
- Use the file name as the link label.

## Completion Checks

Before completion, verify all of these conditions:

- Every public endpoint in the service has exactly one row.
- IDs are sequential with no gaps.
- Every external business API in each runtime path appears in its dependency column.
- Each dependency API line has one aligned entry in its adjacent stub column.
- Every recorded stub ID exists and is selected by the relevant `Booking` data.
- Every service journey spec that calls a public endpoint is linked from the matching row.
- Every populated `testClass` link points to a spec that calls the exact endpoint in that row.
- Every local link resolves.
- Empty `testClass` and `flow doc` cells remain visible as coverage gaps.
- The matrix contains no heading or explanatory prose.
