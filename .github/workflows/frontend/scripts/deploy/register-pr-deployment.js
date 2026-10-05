// Registers a PR-head-bound GitHub Deployment for a comment-triggered ephemeral
// preview, then marks it successful so it links to the PR's Deployments box.
//
// Loaded from the reusable deploy workflow via actions/github-script:
//   const register = require(`${process.env.CI_SCRIPT_ROOT}/deploy/register-pr-deployment.js`);
//   await register({ github, context, core });
//
// Inputs are passed through the step `env:` (never interpolated into the script):
//   APP_NAME     - matrix app name (business-booker | ccui | premier-inn)
//   PR_NUMBER    - PR number driving the <app>-PR-<n> environment name
//   CHECKOUT_REF - immutable PR head SHA the deployment binds to
//   HOST_NAME    - preview host used for the environment URL
module.exports = async ({ github, context, core }) => {
  const environment = `${process.env.APP_NAME}-PR-${process.env.PR_NUMBER}`;
  const ref = process.env.CHECKOUT_REF;
  const host = process.env.HOST_NAME;

  const dep = await github.rest.repos.createDeployment({
    owner: context.repo.owner,
    repo: context.repo.repo,
    ref,
    environment,
    auto_merge: false,
    required_contexts: [],
    transient_environment: true,
    production_environment: false,
    description: 'Ephemeral frontend preview (comment-triggered)',
  });

  if (!dep.data || !dep.data.id) {
    core.warning(`Could not create deployment for ${environment}: ${JSON.stringify(dep.data)}`);
    return;
  }

  await github.rest.repos.createDeploymentStatus({
    owner: context.repo.owner,
    repo: context.repo.repo,
    deployment_id: dep.data.id,
    state: 'success',
    environment,
    environment_url: `https://${host}`,
    description: 'Preview deployed',
  });

  core.info(`Registered PR-scoped deployment ${dep.data.id} for ${environment} at ${ref}.`);
};
