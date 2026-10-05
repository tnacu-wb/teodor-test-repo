# BL001 Migration Insights

Observations and learning points from migrating the first baseline test case (BL001) from the legacy WebDriverIO framework to the new Playwright + TypeScript framework, using Kiro AI as an implementation partner.

## Effort Breakdown

| Phase | Duration | Notes |
|-------|----------|-------|
| Initial framework setup (PI page objects + GraphQL API layer) | 1 day |  |
| Full baseline TC migration (BL001) | 1.5 days | Includes debugging and validation; Test execution time: ~3 minutes|
| Estimated per-TC migration (subsequent PI tests) | 0.5 days | Based on BL001 experience |

## Migration Estimate

- Total test cases to migrate: **120**
- CCUI and Inn Business framework setup: **2–3 days**
- Per-test migration (120 TCs x 0.5 days): **60 days**
- **Total estimated effort: ~63 working days**

## Key Observations

### AI-Assisted Development

1. **Spec refinement is the bottleneck, not implementation.** The formal requirements and design specs required significant manual refinement before AI could produce usable code. This refinement consumed approximately half of the total migration time per test case, though the refinement process itself can be accelerated with AI assistance.

2. **AI implementation speed is high, but output requires validation.** The AI agent produced the initial test implementation in approximately 2–3 hours. However, the generated code contains assumptions (mock data, estimated locators, inferred page structures) that do not always match the live environment.

3. **Step-by-step debugging after implementation is essential.** Because the AI works from assumptions rather than live inspection, a significant portion of the effort goes into running the test against the real environment and correcting locators, timing, and data mismatches incrementally.

4. **Post-debugging code review is recommended.** During the PR process, GitHub Copilot identified additional issues in the committed code that were not caught during manual debugging. A structured review pass after the debugging phase would catch these earlier.

### Tooling Stability

5. **Kiro agent experiences intermittent crashes.** These are recoverable by restarting the Kiro session and do not result in data loss, but they interrupt workflow.

6. **Context window limitations can cause regression.** In one instance the agent lost awareness of the reference framework's location and assumed it did not exist. After a manual reminder, it resumed correctly. Long sessions with large codebases may benefit from periodic context resets or explicit file references.
