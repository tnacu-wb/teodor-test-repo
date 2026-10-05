# Workflows

Workflows for the development team. For the Bitrise backup click [here](bitrise/Bitrise.md).


## Bitrise Workflows

- *Integration-Development* Steps:
    - Running Unit Tests
    - Build scheme Premier Inn (Development)
    - Upload dSYM for development environment
    - App Distribution on Firebase
    - Slack alert

- *Unit-Tests* Steps:
    - Running Unit Tests
    - Slack alert

- *UI-Tests* Steps:
    - Running all (Mock & Regression Pack) UI Tests
    - Slack alert

- *UI-Tests-Mock* Steps:
    - Running default UI Tests
    - Slack alert
        
- *UI-Tests-Regression-Pack* [_triggered every midnight_] Steps:
    - Running "Regression Pack" UI Tests using Live (Development) data
    - Slack alert


## Bitrise Triggers

### Push

- push/merge into `develop` ~> *Integration-Development*
- push/merge into `release/` ~> *Integration*

### Pull Request

- source `any` pull request to `develop` ~> *Unit-Tests*
- source `any` pull request to `release/` ~> *Unit-Tests*

### Tags

- `qa-build` ~> *Integration-Development*



## Useful GIT commands

1. Squash the feature branch:

- on terminal rebase typing`git rebase -i HEAD~#`(_where `#` is the number of the commits to compress into one_)
- press `i` then change `pick` with `s` (_not the first on the top_)
- press `esc` and type `:wq`
- press `i` and change the commit messages removing and editing the comments into one only
- press `esc`and type `:wq`
- then push it on remote typing `git push -f origin feature/DNRQ-XXX-human-readable-description`

2. Rebase feature branch onto *target* branch. On the teerminal:

- Checkout the 'target' branch: ```$ git checkout develop```
- Pull the latest code: ```$ git pull```
- Checkout the feature branch: ```$ git checkout feature/DNRQ-XXX-human-readable-description```
- Rebase develop into the feature branch: ```$ git rebase develop```
- Force push the branch to re-align the commits: ```$ git push --force origin feature/DNRQ-XXX-human-readable-description```
