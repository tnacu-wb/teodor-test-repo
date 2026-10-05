---
parent_design: "none (derived from steering + industry standards)"
jira: CTECH-13417
---
# Implementation Plan: Node.js 24.20.0 Runtime Upgrade

## Revision Note
Re-targeted from Node.js 24.21.0 to 24.20.0 because `node:24.21.0-alpine` was not yet published on Docker Hub; 24.20.0 is the newest runtime with a usable published image. Aligned with reference PR #232, and selected modernisation items were folded into the core upgrade. The spec folder name keeps the original ticket slug (`...-24.21.0`); only the referenced target version changed.

## Overview
Incremental implementation of Node.js runtime upgrade from 22.23.2 to 24.20.0 across the Premier Inn front-end monorepo, focusing on security improvements and maintaining system stability.

## Tasks

- [x] 1. Core Version Updates
  - [x] 1.1 Update Node.js version constraint
    - Update `.nvmrc` from `22.23.2` to `24.20.0`
    - Update `engines.node` in `frontend/pi-front-end-applications/package.json` from `">=22.23.2"` to `">=24.20.0"`
    - Verify `engine-strict=true` remains in `.npmrc`
    - _Requirements: 1.1, 1.2, 1.3_
  - [x] 1.2 Validate local development environment
    - Run `nvm use` and verify Node 24.20.0 loads
    - Run `yarn install` and verify engine constraint enforcement
    - Test basic commands: `yarn build`, `yarn type-check`
    - _Requirements: 1.4, 1.5_

- [x] 2. Docker Infrastructure Updates  
  - [x] 2.1 Update primary Dockerfile
    - Change base image from `node:22.23.2-alpine3.24` to `node:24.20.0-alpine3.24` (retain `-alpine3.24` suffix)
    - Update `npm install --global npm@10.9.9` to `npm install --global npm@11.19.0` (Node 24.20.0 bundles npm 11.19.0)
    - Modernise HEALTHCHECK to use global `fetch` + `AbortSignal.timeout(5000)`, and add `ENV HOSTNAME="0.0.0.0"` so the Next.js standalone server binds correctly and `/api/liveness` passes
    - Verify all RUN steps still execute correctly
    - _Requirements: 2.1, 2.2, 2.3_
  - [x] 2.2 Update secondary Dockerfiles
    - Update `tools/opera-ohip-app/Dockerfile` from `node:22-alpine` to `node:24-alpine` (floating tag resolves to 24.20.x)
    - _Requirements: 2.1_
  - [x] 2.3 Docker build validation
    - Run `docker build -t test-image frontend/pi-front-end-applications/`
    - Run container and verify `/api/liveness` health check passes (modern fetch-based HEALTHCHECK + `HOSTNAME="0.0.0.0"`)
    - _Requirements: 2.4, 2.5_

- [x] 3. CI/CD Pipeline Updates
  - [x] 3.1 Update workflow environment variables
    - Change `NODE_VERSION: "22.23.2"` to `NODE_VERSION: "24.20.0"` in `.github/workflows/ci-fe-deploy.yaml`
    - Change `NODE_VERSION: "22.23.2"` to `NODE_VERSION: "24.20.0"` in `.github/workflows/ci-fe.yaml`
    - _Requirements: 3.1, 3.2_
  - [x] 3.2 GraphQL workflow — descoped (left on Node 22)
    - `.github/workflows/build-graphql.yaml` is intentionally left on `node-version: '22.22.2'`. The GraphQL stack deliberately stays on Node 22 and reference PR #232 does not touch it. No change made — resolved by decision.
    - _Requirements: 3.3_
  - [x] 3.3 Validate CI workflow syntax
    - Run `actionlint` on updated workflows
    - Verify `actions/setup-node` references remain consistent
    - _Requirements: 3.4, 3.5_

- [x] 4. Legacy Configuration Cleanup
  - [x] 4.1 Remove obsolete legacy CI file
    - Remove `frontend/pi-front-end-applications/.github/tests.yaml` (stale Node 14-era config; superseded by root CI workflows)
    - Verify references to this file no longer exist
    - _Requirements: 4.1_
  - [x] 4.2 Audit for additional legacy references  
    - Search codebase for remaining `22.23.2`, or `node.*22` references
    - Exclude generated build artifacts (`.next/`, `.nx/cache/`)
    - Document any remaining references that should not be changed (e.g. GraphQL `22.22.2`)
    - _Requirements: 4.2, 4.3_

- [x] 5. Native Dependency Validation
  - [x] 5.1 Sharp dependency verification
    - Clear node_modules: `find . -name 'node_modules' -type d -prune -exec rm -rf '{}' +`
    - Run `yarn install` and verify sharp compiles without errors
    - Test image processing functionality with sample images
    - _Requirements: 5.1_
  - [x] 5.2 Component catalog build verification  
    - Run `yarn build:atomic:components` and verify all @whitbread-eos/* packages build
    - Check for any native dependency compilation errors
    - _Requirements: 5.2_
  - [x] 5.3 Application build verification
    - Run `yarn build:pi`, `yarn build:ccui`, `yarn build:bb` individually  
    - Verify all three applications build successfully
    - Check for any runtime or compilation warnings
    - _Requirements: 5.3, 5.4, 5.5_

- [ ] 6. Comprehensive Validation Gate
  - [ ] 6.1 Full installation and build validation
    - Run `yarn build:all` (clean install + type gen + component build) on the aligned 24.20.0 code
    - Verify zero installation errors and zero build errors
    - _Requirements: 6.1, 6.2_
  - [ ] 6.2 Code quality validation
    - Run `yarn type-check` and verify zero TypeScript errors
    - Run `yarn lint` and verify zero linting errors (or `yarn lint:fix` if needed)
    - _Requirements: 6.3, 6.4_
  - [ ] 6.3 Test suite validation
    - Run `yarn test:ci` and verify all tests pass
    - Check for any test timeouts or flakiness introduced by Node version change
    - _Requirements: 6.5_
  - [ ] 6.4 Full integration validation
    - Run `yarn check-integrity:all` (comprehensive validation pipeline)
    - Build and test Docker image with health checks
    - _Requirements: 6.6, 6.7_

- [ ] 7. Rollback Preparation and Testing
  - [ ] 7.1 Document rollback procedures
    - Create rollback checklist for `.nvmrc`, `package.json`, Dockerfile changes
    - Document CI environment variable reversion steps  
    - Test rollback procedure in isolated environment
    - _Requirements: 7.1, 7.2, 7.3_
  - [ ] 7.2 Validate rollback functionality
    - Temporarily revert changes and verify system returns to Node 22.23.2
    - Run validation suite on rolled-back version
    - Re-apply Node 24 changes after successful rollback test
    - _Requirements: 7.4, 7.5_

- [x] 8. Modernisation Enhancements
  - [x] 8.1 npm version alignment (folded into core upgrade)
    - Updated `npm install --global npm@10.9.9` to `npm@11.19.0` in the Dockerfile to match Node 24.20.0's bundled npm. Done as part of the core upgrade (reference PR #232).
    - _Requirements: 8.1_
  - [x] 8.2 Dependency audit and cleanup (partially folded into core upgrade)
    - DONE (folded in): Removed the unused `v8` npm dependency from premier-inn and changed `import v8 from 'v8'` to `import v8 from 'node:v8'` in `memory.js` and `memory.test.js`. Bumped `@types/node` `22.10.2 → 24.13.3` across all three apps. Refactored `scripts/runtimeEnvVars` onto Node built-ins (`node:util` parseArgs + styleText), dropping `glob` and `yargs`, marked `type: module` with `engines.node >=24.20.0` — which also removed the `npm install` layer from the Dockerfile and `.github/frontend/actions/build/pre-build.sh`.
    - _Requirements: 8.2, 8.4, 8.5_
  - [x] 8.3 Node.js 24 feature evaluation
    - Research new Node.js 24 APIs that could benefit build performance
    - Assess any new npm features that could improve the development experience
    - Document findings for potential future implementation
    - _Requirements: 8.3_
  - [x] 8.4 CI/CD optimisation opportunities
    - Consider testing matrix with multiple Node versions during transition period
    - Evaluate caching strategies that might benefit from Node 24
    - Review action versions for Node 24 compatibility
    - _Requirements: 8.6_

## Notes

**Testing approach**: Each task includes verification steps to ensure changes work before proceeding. The core security upgrade (tasks 1-7) should be completed and validated before attempting remaining optional modernisation tasks.

**Native dependency focus**: Sharp is the highest-risk dependency for Node version upgrades. Task 5.1 should be executed carefully with fallback plans if compilation fails.

**CI/CD safety**: Task 3 changes should be tested in a feature branch before merging to main branches to avoid breaking builds. `build-graphql.yaml` is deliberately excluded — the GraphQL stack stays on Node 22.

**Rollback readiness**: Task 7 ensures quick reversion capability if production issues arise after deployment.

## Task Dependency Graph
```json
{
  "waves": [
    {
      "id": 0,
      "tasks": ["1.1", "4.1", "4.2"]
    },
    {
      "id": 1,
      "tasks": ["1.2", "2.1", "2.2", "3.1", "3.2"]
    },
    {
      "id": 2,
      "tasks": ["2.3", "3.3", "5.1"]
    },
    {
      "id": 3,
      "tasks": ["5.2", "5.3"]
    },
    {
      "id": 4,
      "tasks": ["6.1", "6.2", "6.3"]
    },
    {
      "id": 5,
      "tasks": ["6.4", "7.1"]
    },
    {
      "id": 6,
      "tasks": ["7.2"]
    },
    {
      "id": 7,
      "tasks": ["8.1", "8.2", "8.3", "8.4"]
    }
  ]
}
```
