// Adds the 'ephemeral-active' label to a PR so later commits redeploy the
// ephemeral environment. Creates the repo label on first use, then retries once.
//
// Loaded from the reusable deploy workflow via actions/github-script:
//   const markPrEphemeral = require(`${process.env.CI_SCRIPT_ROOT}/deploy/mark-pr-ephemeral.js`);
//   await markPrEphemeral({ github, context, core });
//
// Inputs are passed through the step `env:` (never interpolated into the script):
//   PR_NUMBER - PR number to label
module.exports = async ({ github, context, core }) => {
  const prNumber = Number(process.env.PR_NUMBER);
  if (!Number.isInteger(prNumber) || prNumber <= 0) {
    core.info('No valid PR number supplied; skipping label.');
    return;
  }

  const label = 'ephemeral-active';
  const addLabel = () =>
    github.rest.issues.addLabels({
      owner: context.repo.owner,
      repo: context.repo.repo,
      issue_number: prNumber,
      labels: [label],
    });

  try {
    await addLabel();
    core.info(`Labelled PR #${prNumber} with '${label}'.`);
  } catch (e) {
    // First-ever use: the repo label may not exist. Create it, then retry once.
    if (e.status === 404 || e.status === 422) {
      await github.rest.issues
        .createLabel({
          owner: context.repo.owner,
          repo: context.repo.repo,
          name: label,
          color: '0e8a16',
          description: 'PR has a frontend ephemeral environment; redeploy on new commits.',
        })
        .catch(() => {});
      await addLabel();
      core.info(`Created label '${label}' and applied it to PR #${prNumber}.`);
    } else {
      throw e;
    }
  }
};
