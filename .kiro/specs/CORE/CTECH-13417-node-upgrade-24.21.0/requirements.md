---
parent_design: "none (derived from steering + industry standards)"
jira: CTECH-13417
---
# Requirements Document

## Revision Note
This upgrade was re-targeted from Node.js 24.21.0 to 24.20.0 because the `node:24.21.0-alpine` image was not yet published on Docker Hub, so 24.20.0 is the newest runtime with a usable published image. The change aligns with reference PR #232, which also folded selected modernisation items into the core upgrade. The spec folder name retains the original ticket slug (`...-24.21.0`); only the target version referenced inside the documents has changed.

## Introduction
This specification defines the requirements for upgrading the Node.js runtime from version 22.23.2 to 24.20.0 across the Premier Inn front-end monorepo. This upgrade addresses security vulnerabilities in Node 22 (approaching end-of-maintenance) and enables modernisation opportunities. The scope covers three Next.js applications (premier-inn, business-booker, ccui), shared @whitbread-eos/* catalog packages, CI/CD infrastructure, and Docker containers. References Jira ticket CTECH-13417.

## Glossary
- **Runtime upgrade**: Changing the Node.js version across all environments and tooling
- **Native dependencies**: Node.js modules that require compilation (e.g. sharp, node-gyp)
- **Engine constraint**: The `engines.node` field in package.json that enforces minimum Node version
- **Yarn workspaces**: The monorepo package management structure
- **Atomic components**: The @whitbread-eos/* shared component packages

## Requirements

### Requirement 1: Core Runtime Version Update
**User Story:** As a developer, I want to use Node.js 24.20.0 as the standard runtime, so that the application benefits from the latest security patches and performance improvements.

#### Acceptance Criteria
1.1. THE system SHALL pin Node.js version to exactly 24.20.0 in `.nvmrc`
1.2. THE system SHALL update `engines.node` constraint in root `package.json` to `>=24.20.0`
1.3. THE system SHALL maintain `engine-strict=true` enforcement in `.npmrc`
1.4. WHEN developers run `nvm use` THEN the system SHALL load Node.js 24.20.0
1.5. WHEN `yarn install` is executed THEN the system SHALL enforce the new engine constraint

### Requirement 2: Docker Container Updates
**User Story:** As a platform engineer, I want Docker images to use Node.js 24.20.0, so that deployed applications run on the target runtime.

#### Acceptance Criteria
2.1. THE system SHALL update the base image in `frontend/pi-front-end-applications/Dockerfile` to `node:24.20.0-alpine3.24` (retaining the `-alpine3.24` suffix)
2.2. THE system SHALL update the npm version pin to `npm install --global npm@11.19.0` to align with Node 24.20.0's bundled npm version
2.3. THE system SHALL modernise the Docker HEALTHCHECK to use the global `fetch` API with `AbortSignal.timeout(5000)`, and SHALL add `ENV HOSTNAME="0.0.0.0"` so the Next.js standalone server binds correctly and the `/api/liveness` liveness probe passes
2.4. WHEN Docker builds are executed THEN the container SHALL use Node.js 24.20.0
2.5. WHEN the container starts THEN health checks at `/api/liveness` SHALL pass

### Requirement 3: CI/CD Infrastructure Updates
**User Story:** As a CI/CD engineer, I want all front-end build pipelines to use Node.js 24.20.0, so that builds are consistent across environments.

#### Acceptance Criteria
3.1. THE system SHALL update the `NODE_VERSION` environment variable in `.github/workflows/ci-fe-deploy.yaml` to "24.20.0"
3.2. THE system SHALL update the `NODE_VERSION` environment variable in `.github/workflows/ci-fe.yaml` to "24.20.0"
3.3. THE system SHALL leave `.github/workflows/build-graphql.yaml` on Node `22.22.2` — the GraphQL stack deliberately stays on Node 22 and is out of scope for this upgrade
3.4. WHEN front-end CI workflows reference Node version THEN they SHALL use the updated version consistently
3.5. WHEN using `actions/setup-node` with `node-version-file` THEN it SHALL read from the updated `.nvmrc`

### Requirement 4: Legacy Configuration Cleanup
**User Story:** As a maintainer, I want obsolete configuration modernised, so that the codebase stays clean and maintainable.

#### Acceptance Criteria
4.1. THE system SHALL modernise the `frontend/pi-front-end-applications/.github/tests.yaml` workflow file: remove the stale Node 14.x matrix and use `actions/setup-node` with `node-version-file: .nvmrc`
4.2. THE system SHALL NOT modify any generated build artifacts (`.next/`, `.nx/cache/`)
4.3. WHEN configuration files are audited THEN obsolete Node version references SHALL be removed

### Requirement 5: Native Dependency Compatibility
**User Story:** As a developer, I want all native dependencies to work with Node.js 24, so that builds and runtime functionality are preserved.

#### Acceptance Criteria
5.1. THE system SHALL verify `sharp` compiles and runs correctly with Node.js 24.20.0
5.2. THE system SHALL verify all packages in `pi-components-catalog/*` build successfully
5.3. THE system SHALL verify all three applications (premier-inn, business-booker, ccui) build successfully
5.4. WHEN native dependencies are installed THEN compilation SHALL complete without errors
5.5. IF any native dependency fails THEN a compatibility update or replacement SHALL be identified

### Requirement 6: Comprehensive Verification
**User Story:** As a quality engineer, I want full validation that the upgrade preserves all functionality, so that no regressions are introduced.

#### Acceptance Criteria
6.1. THE system SHALL pass `yarn install` on Node.js 24.20.0 with zero errors
6.2. THE system SHALL pass `yarn build` for all packages with zero errors
6.3. THE system SHALL pass `yarn type-check` with zero type errors
6.4. THE system SHALL pass `yarn lint` with zero linting errors
6.5. THE system SHALL pass `yarn test:ci` with all tests passing
6.6. THE system SHALL build Docker images successfully
6.7. THE system SHALL pass container health checks

### Requirement 7: Rollback Capability
**User Story:** As a platform engineer, I want the ability to rollback the Node upgrade, so that production stability is protected if issues arise.

#### Acceptance Criteria
7.1. THE system SHALL maintain the ability to revert `.nvmrc` to 22.23.2
7.2. THE system SHALL maintain the ability to revert Docker base image tags
7.3. THE system SHALL maintain the ability to revert CI environment variables
7.4. WHEN rollback is executed THEN all environments SHALL return to the previous Node version
7.5. WHEN rollback is completed THEN all verification steps SHALL pass on Node 22.23.2

### Requirement 8: Modernisation Opportunities
**User Story:** As a developer, I want to take advantage of Node.js 24 features and tooling improvements, so that the development experience is enhanced.

Note: Several items below were folded into the core upgrade as part of reference PR #232 rather than being deferred (see design doc Modernisation section).

#### Acceptance Criteria
8.1. THE system SHALL align the npm version with Node 24.20.0's bundled npm (`11.19.0`)
8.2. THE system SHALL remove deprecated or redundant dependencies where identified (the unused `v8` npm dependency was removed from premier-inn and `import v8 from 'v8'` changed to `import v8 from 'node:v8'`; the `scripts/runtimeEnvVars` tool was refactored onto Node built-ins, dropping the `glob` and `yargs` dependencies)
8.3. THE system SHOULD assess opportunities to leverage new Node.js 24 APIs or performance features
8.4. THE system SHOULD consolidate any duplicate tooling configurations
8.5. THE system SHALL bump `@types/node` from `22.10.2` to `24.13.3` across all three apps
8.6. WHERE genuinely optional modernisation items remain THEN they SHALL be clearly scoped as separate from the core upgrade
