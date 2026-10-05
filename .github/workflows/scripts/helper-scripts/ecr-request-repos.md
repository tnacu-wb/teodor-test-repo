### Auto-create ECR repos before deployment in digital-monorepo

---

### Developers FYI

✅ **What you need to do:**

* create your service in `backend/<pillar>/services/<your-service>`
* a directory is only recognised as a service once it contains `pom.xml`
* name the directory in lowercase; must match `^[a-z0-9._-]+$` (e.g. `hotel-review-service`)
* this becomes your ECR repo `whitbreaddigital/<your-service>`
* open your PR and get it merged to main as normal

🚫 **You do NOT need to:**

* open a PR in IaC repo `devops-terraform`
* create the ECR repo, its lifecycle policy, or tags by hand
* the Terraform module applies the ECR repo standard config
* do anything for an existing service;
  if ECR repo already exists, so `request-ecr-repos` job is skipped for every subsequent build

⚠️ **Good to know:**

* repos are created on merge to `main`, `release/*` and `hotfix/*`, not from an open PR; 
  so a new service's ephemeral PR environment cannot push an image until it has merged once; 
  (existing services' ephemeral deploys are unaffected)
* renaming a service is treated as a new service: a fresh ECR repo `whitbreaddigital/<new-name>` is created on merge, 
  while the old repo (and any images in it) is left in place
* if ECR repo creation fails (e.g. failed Terraform apply), your build is blocked on purpose rather than pushing to a non-existent ECR repo; 
  pipeline automatically rolls back `devops-terraform` changes. 
  **Fix and re-run**

---

### Components

* **Detection** - `.github/workflows/scripts/detect-all.sh`
Updated to include handling `new_services`, 
i.e service directories whose `pom.xml` was added in current push.
Only runs for push events.

* **Orchestration** - `.github/workflows/ci-be.yaml`
New `request-ecr-repos` job runs when `new_services` is not empty, **before** `build-services`.
`build-services` job `needs` it, so images are only built/pushed once the repo(s) are confirmed to exist.

  | `request-ecr-repos` | when | `build-services` |
  |---|---|---|
  | `skipped` | no new services | ✅ runs |
  | `success` | created new repo(s) | ✅ runs |
  | `failure` | ECR request/apply failed | ⛔ blocked |
  | `cancelled` | run cancelled | ⛔ blocked |

* **ECR repo request** - `.github/workflows/ecr-request-repos.yaml`

    - validates each name (`^[a-z0-9._-]+$`); 
      any invalid name fails the whole run before anything is created
    - filters out names already declared in `devops-terraform`'s `repositories.yaml`
    - batches all new repos into a single branch/PR against `devops-terraform`
      (`auto/ecr-add-<hash>`, exact set of repo names is hashed)  ###
    - verifies the `terragrunt plan` comment;
      asserts exactly `N×3 to add, 0 to change, 0 to destroy` before allowing the merge
      (N = number of new repos)
    - admin-merges the PR via `pipeline-and-clusters[bot]` GitHub App
    - waits for `terragrunt apply` to succeed,
      confirming the repo exists in AWS, not just declared in code
    - rolls back (reverting the merge) if apply fails, 
      and fails the job so the build is blocked

---

### Functionality

* **Request ECR repos via single PR**
    - validate every requested name; reject the batch on any invalid name
    - exit early with `already-declared` (no PR) if every name is already in `repositories.yaml` (safe to re-run)
    - derive JIRA ticket from caller's branch/commit for PR naming
    - append all new names, commit, push `auto/ecr-add-<hash>`, open one PR
* **Verify terragrunt plan — poll the PR comment (15s, up to 5 min); assess `N×per-repo-to-add / 0 / 0`**
* **Merge request PR - admin squash-merge via GitHub App, record the merge SHA**
* **Wait for `terragrunt apply` - poll `devops-terraform` pipeline (20s, up to 10 min) on the merge SHA until it succeeds**
* **Reuse-or-create (failed run recovery) - a re-run over the same set reuses the still open request PR instead of opening a duplicate**
* **Revert on failed apply - open + merge a revert PR restoring `repositories.yaml` and still fail the job so the failure surfaces in the pipeline**

```
push to main
   └─ detect-all ──> new_services = ["svc-a","svc-b"]
        └─ request-ecr-repos (single PR to devops-terraform IaC: svc-a + svc-b)
             plan (6 to add) → merge → apply ✔ (devops-terraform IaC)
                └─ build-services (matrix) → docker build → push whitbreaddigital/svc-*
```
