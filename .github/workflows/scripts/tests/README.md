# Script tests — main-branch versioning

[bats-core](https://github.com/bats-core/bats-core) tests for the pure shell
helpers backing the `ci-main-versioning.yaml` workflow.

## Layout

```
tests/
├── README.md                       # this file
├── helpers/
│   └── generators.bash             # shared generator harness (random semvers
│                                   #   and tag sets)
├── compute-next-version.bats       # P1, P2 + example/unit tests (tasks 1.2-1.4)
├── retry-push.bats                 # retry helper unit tests       (task 2.2)
└── resolve-image-tag.bats          # P3 main-line tag == version  (task 3.2)
```

Only `helpers/generators.bash` exists so far; the individual `.bats` files are
added by their corresponding property/unit-test tasks in
`.kiro/specs/monorepo/main-branch-versioning/tasks.md`.

## Conventions

- **Property tests** run a minimum of **100 generated iterations** and are tagged
  with a comment referencing the design property, format:
  `Feature: main-branch-versioning, Property {number}: {property_text}`.
- Tests source the shared harness with `load "helpers/generators"`.
- Scripts under test are invoked as subprocesses and asserted on their
  `$GITHUB_OUTPUT` value and exit code.

## Running

```bash
# from repo root, with bats-core installed
bats .github/workflows/scripts/tests
```

If the team prefers Python + `hypothesis` driving the scripts via subprocess,
that is an acceptable substitute per the design's Testing Strategy.
