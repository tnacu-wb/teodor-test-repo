// Posts the PR link and the "Steps to Replicate / Verify" section to the linked
// issue after an ephemeral preview deploys. Posts once across the per-app deploy
// matrix using a hidden marker.
//
// Loaded from the reusable deploy workflow via actions/github-script:
//   const postIssueVerification = require(`${process.env.CI_SCRIPT_ROOT}/notifications/post-issue-verification.js`);
//   await postIssueVerification({ github, context, core });
//
// Inputs are passed through the step `env:` (never interpolated into the script):
//   ISSUE_NUMBER - linked issue number derived by deployment-identity
//   PR_NUMBER    - PR number that triggered the deploy
module.exports = async ({ github, context, core }) => {
  const issueNumber = Number(process.env.ISSUE_NUMBER);
  if (!issueNumber) {
    core.info('No linked issue number; skipping issue comment.');
    return;
  }

  const prNumber = Number(process.env.PR_NUMBER);
  if (!prNumber) {
    core.info('No PR number supplied; skipping issue comment.');
    return;
  }

  // The comment entry point has no pull_request in the event payload, so the
  // PR URL/body/number are fetched from the API by number instead.
  const { data: pr } = await github.rest.pulls.get({
    owner: context.repo.owner,
    repo: context.repo.repo,
    pull_number: prNumber,
  });
  const prUrl = pr.html_url;
  const prBody = pr.body || '';

  // Extract the "Steps to Replicate / Verify" section from the PR description.
  let replicationSteps = '';
  const match = prBody.match(/###\s*Steps to Replicate[^\n]*\n([\s\S]*?)(?=\n###\s|$)/i);
  if (match && match[1]) {
    replicationSteps = match[1].trim();
  }

  // Hidden marker so we only post once across the per-app deploy matrix.
  const marker = `<!-- kiro-issue-comment:pr-${pr.number} -->`;

  const existing = await github.paginate(github.rest.issues.listComments, {
    owner: context.repo.owner,
    repo: context.repo.repo,
    issue_number: issueNumber,
    per_page: 100,
  });
  if (existing.some((c) => (c.body || '').includes(marker))) {
    core.info(`Issue #${issueNumber} already has the PR comment; skipping.`);
    return;
  }

  const stepsSection = replicationSteps
    ? `\n\n### Steps to Replicate / Verify\n${replicationSteps}`
    : '\n\n_No "Steps to Replicate / Verify" section was found in the PR description._';

  const body = `${marker}\nA pull request has been opened for this issue: ${prUrl}${stepsSection}`;

  await github.rest.issues.createComment({
    owner: context.repo.owner,
    repo: context.repo.repo,
    issue_number: issueNumber,
    body,
  });
  core.info(`Posted PR link and replication steps to issue #${issueNumber}.`);
};
