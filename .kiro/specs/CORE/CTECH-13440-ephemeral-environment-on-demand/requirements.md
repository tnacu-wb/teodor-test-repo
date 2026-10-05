---
parent_design: "none (derived from steering + existing frontend CI workflows)"
jira: CTECH-13440
---
# Requirements Document

## Introduction

Today the frontend CI pipeline (`.github/workflows/ci-fe.yaml`) deploys an ephemeral
environment for **every** pull request against `main` (or `test-deployment`), across all
three Next.js applications (`premier-inn`, `business-booker`, `ccui`). Because the dev
cluster has limited resources, deploying on every PR is wasteful — most PRs never need a
running preview.

This specification changes ephemeral frontend deployment from "always on every PR" to
**on demand**. An ephemeral environment is only created when one of two explicit signals is
present:

1. The PR source branch name ends with a recognised proof-of-concept (POC) suffix
   (`-JIRA-XX` or `-Issues-XX` or `-TEAMS-XX`) — these branches always get an ephemeral environment
   automatically as part of the PR pipeline.
2. For any other PR, a reviewer or the author comments the trigger phrase
   (`Ephemeral UP`) on the PR — this triggers the same deployment that runs today.

PRs that have neither signal must **not** create an ephemeral environment. When a deployment
is triggered, it must behave exactly as the current per-PR deployment does (same image build,
Helm chart, values, naming, PR/issue/Jira feedback, and cleanup). Scope is **frontend only**.
References Jira ticket CTECH-13440.

## Glossary

- **Ephemeral environment**: A temporary Kubernetes/Helm deployment of a frontend app in the
  `opera-fe` namespace, used as a per-PR preview. Named via the existing
  `.github/frontend/actions/deployment-identity` action.
- **On-demand deployment**: Ephemeral deployment that only happens when an explicit trigger
  (POC branch suffix or PR comment) is present, rather than on every PR.
- **POC branch suffix**: A branch name ending in `-JIRA-XX` or `-Issues-XX` or `-TEAMS-XX`, already used in
  the pipeline to mark proof-of-concept work.
- **Trigger comment**: A PR comment containing the exact trigger phrase `Ephemeral UP` that
  requests an ephemeral deployment.
- **Frontend apps**: `premier-inn`, `business-booker`, `ccui`.
- **Current deployment behaviour**: The `deploy-frontend-ephemeral` job in `ci-fe.yaml` plus
  its downstream feedback (`report-ephemeral-deployment-jira`, PR/issue comments) as it exists
  today.

## Requirements

### Requirement 1: Ephemeral deployment is off by default per PR

**User Story:** As a platform engineer, I want ephemeral frontend environments to NOT be
created on every PR, so that the dev cluster is not overloaded by previews nobody uses.

#### Acceptance Criteria

1.1. WHEN a pull request is opened, synchronised, or reopened against `main` or
`test-deployment` AND the branch name does NOT end with `-JIRA-XX` or `-Issues-XX` or `-TEAMS-XX` AND no
valid trigger comment has been posted THEN the system SHALL NOT deploy an ephemeral
environment for any frontend app.
1.2. THE system SHALL continue to run the non-deployment PR checks (build, and the
scan/analysis jobs subject to their existing conditions) regardless of whether an ephemeral
deployment is triggered.
1.3. WHEN no ephemeral deployment is triggered THEN the system SHALL NOT create GitHub
deployment environments, Helm releases, or post preview-URL comments for that PR.
1.4. THE change SHALL apply uniformly to all three frontend apps (`premier-inn`,
`business-booker`, `ccui`).

### Requirement 2: Automatic ephemeral deployment for POC branch suffixes

**User Story:** As a developer working on a POC branch, I want my ephemeral environment to be
created automatically, so that proof-of-concept branches keep their current fast preview flow
without any manual step.

#### Acceptance Criteria

2.1. WHEN a pull request source branch name ends with `-JIRA-XX` OR `-Issues-XX` or `-TEAMS-XX` THEN the
system SHALL automatically deploy the ephemeral environment as part of the PR pipeline.
2.2. THE system SHALL NOT skip ephemeral deployment for POC-suffixed branches, even though
those branches currently skip other validation jobs (dependency review, code analysis,
quality scans, Prisma scans).
2.3. THE automatic POC deployment SHALL use the identical build, image, Helm, and
deployment-identity flow as the current per-PR deployment.
2.4. THE system SHALL preserve existing POC-branch behaviour, including the
`-JIRA-XX` Jira deployment report (`report-ephemeral-deployment-jira`) and the existing
suppression of the standard preview-URL PR comment for `-JIRA-XX` branches.
2.5. WHEN a POC-suffixed branch is updated with new commits THEN the system SHALL redeploy the
ephemeral environment to reflect the latest changes.

### Requirement 3: Comment-triggered ephemeral deployment

**User Story:** As a reviewer or PR author, I want to deploy an ephemeral environment by
commenting on the PR, so that I can get a preview only when I actually need one.

#### Acceptance Criteria

3.1. WHEN a comment containing the exact trigger phrase `Ephemeral UP` is posted on an open
pull request targeting `main` or `test-deployment` THEN the system SHALL trigger an ephemeral
deployment for the frontend apps.
3.2. THE comment-triggered deployment SHALL produce the same result as the current per-PR
deployment (same image build from the PR head, Helm release, host naming, and PR feedback).
3.3. THE system SHALL deploy the current head commit of the pull request at the time the
trigger comment is processed.
3.4. WHEN the trigger phrase does not match (e.g. different wording or casing outside the
agreed matching rule) THEN the system SHALL NOT trigger a deployment.
3.5. WHEN a trigger comment is posted on a PR whose branch already qualifies under Requirement
2 (POC suffix) THEN the system SHALL deploy without creating duplicate/conflicting releases
for the same app and PR.
3.6. THE system SHALL acknowledge a valid trigger (e.g. reaction or comment) so the user knows
the deployment has started, and SHALL report the outcome (success with URL, or failure).
3.7. WHEN a comment-triggered deployment is authorised THEN the system SHALL build and deploy
the immutable PR head commit pinned by its SHA — never a mutable merge ref — so the exact
reviewed commit is what gets built and deployed, closing the time-of-check/time-of-use (TOCTOU)
risk. Mergeability is used only to flag out-of-sync status (see 3.8), not to change what is
checked out.
3.8. WHEN the pull request has unresolved merge conflicts with its base branch THEN the system
SHALL NOT attempt a merge or rebase, SHALL deploy the PR branch head as-is, AND SHALL post a
comment on the PR notifying that the branch is not in sync with its base branch and should be updated.

### Requirement 4: Reuse existing deployment infrastructure and behaviour

**User Story:** As a maintainer, I want on-demand deployment to reuse the existing frontend
CI/CD components unchanged where possible, so that maintenance burden stays low and behaviour
stays consistent.

#### Acceptance Criteria

4.1. THE system SHALL reuse the existing `.github/frontend/actions/deployment-identity` action
for release, host, chart, values, and image naming.
4.2. THE system SHALL reuse the existing image build/publish flow
(`.github/frontend/actions/container-image`) and Helm deploy scripts
(`.github/frontend/scripts/deploy`).
4.3. THE system SHALL deploy to the existing `opera-fe` namespace using the existing shared
Helm chart and ephemeral values.
4.4. THE system SHALL continue to use the existing GitHub App tokens and permissions model
used by the current deploy job.
4.5. THE system SHALL keep the existing release/host naming keyed to the app and PR number so
that environments remain discoverable in the repository's Environments page.

### Requirement 5: Cleanup of on-demand environments

**User Story:** As a platform engineer, I want on-demand ephemeral environments to be cleaned
up the same way per-PR environments are today, so that resources are reclaimed and no orphaned
releases accumulate.

#### Acceptance Criteria

5.1. WHEN a pull request that has an ephemeral environment is closed or merged THEN the system
SHALL clean up the environment using the existing
`.github/frontend/actions/cleanup-preview` flow (`cleanup-pr-fe.yaml`).
5.2. THE scheduled stale-PR cleanup SHALL continue to clean up on-demand environments using
its existing criteria.
5.3. THE cleanup SHALL correctly target environments created by both trigger paths (POC suffix
and trigger comment) using the same naming convention.
5.4. WHEN a PR never created an ephemeral environment THEN cleanup SHALL be a no-op for that PR
and SHALL NOT error.

### Requirement 6: Access control for the comment trigger

**User Story:** As a security-conscious engineer, I want only authorised people to trigger
ephemeral deployments by comment, so that the trigger cannot be abused to consume cluster
resources.

#### Acceptance Criteria

6.1. THE system SHALL only act on trigger comments from users with write access (or higher) to
the repository.
6.2. WHEN a trigger comment is posted by a user without the required permission THEN the system
SHALL NOT deploy and SHOULD indicate that the request was ignored.
6.3. THE comment-triggered deployment SHALL only run against open pull requests targeting the
supported base branches (`main`, `test-deployment`).
6.4. THE system SHALL NOT allow the comment trigger to deploy to production namespaces or
override protected environment configuration.

### Requirement 7: Feedback and observability

**User Story:** As a PR author, I want clear feedback on whether my ephemeral environment was
deployed and where to reach it, so that I can use or troubleshoot it without digging through
CI logs.

#### Acceptance Criteria

7.1. WHEN an on-demand deployment succeeds for a non-`-JIRA-XX` branch THEN the system SHALL
post the preview URL(s) to the PR, matching current behaviour.
7.2. WHEN an on-demand deployment succeeds for a `-JIRA-XX` branch THEN the system SHALL post
the deployment report to Jira using the existing notification script, matching current
behaviour.
7.3. WHEN an on-demand deployment fails THEN the system SHALL surface the failure in the
workflow run and to the triggering PR/comment context.
7.4. THE system SHALL preserve the existing linked-issue verification comment behaviour
(`issue-number` path) for deployments that resolve a linked issue.
7.5. THE system SHALL preserve the existing Teams workflow status reporting for the frontend
pipeline.
