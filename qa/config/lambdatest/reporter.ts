/**
 * LambdaTest Test Manager Reporter
 *
 * Custom Playwright reporter that collects test results mapped to LambdaTest
 * Test Manager test cases via @TC-xxx tags in test titles.
 *
 * Outputs a structured JSON file that can be consumed by:
 * - Kiro (reads JSON → pushes via LambdaTest MCP)
 * - CI scripts (reads JSON → pushes via REST API)
 *
 * Zero manual configuration needed beyond LT_USERNAME/LT_ACCESS_KEY
 * (which are already required for LambdaTest Cloud execution).
 *
 * How it works:
 * 1. Hooks into Playwright's onTestEnd lifecycle
 * 2. Extracts @TC-xxx from each test title
 * 3. Categorises tests by suite tag (@smoke, @regression, or feature)
 * 4. Writes reports/lambdatest-results.json with full results
 * 5. Prints a console summary
 *
 * Usage in playwright.config.ts:
 *   reporter: [['./src/reporters/lambdatest-reporter.ts']]
 */

import type {
  FullConfig,
  FullResult,
  Reporter,
  Suite,
  TestCase,
  TestResult,
} from '@playwright/test/reporter';
import { existsSync, mkdirSync, writeFileSync } from 'fs';
import { join } from 'path';

// ─── Constants (auto-derived, no env vars needed) ─────────────────────────────

const PROJECT_ID = '01M084W4B40G3RM7E5T4X4C2QR'; // CORE QA POC

// Folder IDs in LambdaTest Test Manager (mapped from test suite tags)
const RUN_FOLDERS: Record<string, string> = {
  smoke: '01M086YX68BXPN85T4M8Z8S4K0',    // UAT folder (test-runs)
  regression: '01M086YX68BXPN85T4M8Z8S4K0', // UAT folder (test-runs)
  feature: '01M086YX68BXPN85T4M8Z8S4K0',    // UAT folder (test-runs)
};

// ─── Types ────────────────────────────────────────────────────────────────────

interface TestCaseResult {
  tcId: string;        // e.g. "TC-789"
  title: string;       // Full test title
  suite: string;       // "smoke" | "regression" | "feature"
  status: 'Passed' | 'Failed' | 'Skipped';
  duration: number;    // ms
  remarks: string;     // Pass message or error snippet
  file: string;        // Test file path
}

interface ResultsReport {
  runName: string;
  projectId: string;
  app: string;
  env: string;
  timestamp: string;
  summary: {
    total: number;
    passed: number;
    failed: number;
    skipped: number;
  };
  results: TestCaseResult[];
}

// ─── Regex ────────────────────────────────────────────────────────────────────

const TC_TAG_REGEX = /@TC-(\d+)/;
const SUITE_TAG_REGEX = /@(smoke|regression)/;

// ─── Reporter ─────────────────────────────────────────────────────────────────

export default class LambdaTestReporter implements Reporter {
  private results: TestCaseResult[] = [];
  private app: string;
  private env: string;
  private outputDir: string;

  constructor() {
    this.app = (process.env.APP ?? 'pi').toUpperCase();
    this.env = (process.env.ENV ?? 'uat').toUpperCase();
    this.outputDir = join(process.cwd(), 'reports');
  }

  onBegin(_config: FullConfig, _suite: Suite): void {
    console.log(`\n[LambdaTest Reporter] Collecting results for ${this.app} | ${this.env}`);
  }

  onTestEnd(test: TestCase, result: TestResult): void {
    // Only process tests with @TC-xxx tags
    const tcMatch = test.title.match(TC_TAG_REGEX);
    if (!tcMatch) return;

    const tcId = `TC-${tcMatch[1]}`;

    // Determine suite category
    const suiteMatch = test.title.match(SUITE_TAG_REGEX);
    const suite = suiteMatch ? suiteMatch[1] : 'feature';

    // Map Playwright status to LambdaTest status
    let status: TestCaseResult['status'];
    let remarks: string;

    switch (result.status) {
      case 'passed':
        status = 'Passed';
        remarks = `Passed in ${(result.duration / 1000).toFixed(1)}s`;
        break;
      case 'failed':
      case 'timedOut':
        status = 'Failed';
        remarks = result.errors?.[0]?.message?.slice(0, 500) ?? 'Test failed';
        break;
      case 'skipped':
        status = 'Skipped';
        remarks = 'Test skipped';
        break;
      default:
        status = 'Skipped';
        remarks = `Status: ${result.status}`;
    }

    this.results.push({
      tcId,
      title: test.title,
      suite,
      status,
      duration: result.duration,
      remarks,
      file: test.location.file,
    });
  }

  async onEnd(_result: FullResult): Promise<void> {
    if (this.results.length === 0) {
      console.log('[LambdaTest Reporter] No tests with @TC-xxx tags found — nothing to report.');
      return;
    }

    // Build the report
    const report: ResultsReport = {
      runName: `${this.app}-${this.env}-${new Date().toISOString().slice(0, 16)}`,
      projectId: PROJECT_ID,
      app: this.app,
      env: this.env,
      timestamp: new Date().toISOString(),
      summary: {
        total: this.results.length,
        passed: this.results.filter((r) => r.status === 'Passed').length,
        failed: this.results.filter((r) => r.status === 'Failed').length,
        skipped: this.results.filter((r) => r.status === 'Skipped').length,
      },
      results: this.results,
    };

    // Ensure output directory exists
    if (!existsSync(this.outputDir)) {
      mkdirSync(this.outputDir, { recursive: true });
    }

    // Write JSON report
    const outputPath = join(this.outputDir, 'lambdatest-results.json');
    writeFileSync(outputPath, JSON.stringify(report, null, 2));

    // Print summary
    console.log(`\n[LambdaTest Reporter] Results Summary`);
    console.log(`  Run:     ${report.runName}`);
    console.log(`  Total:   ${report.summary.total}`);
    console.log(`  Passed:  ${report.summary.passed}`);
    console.log(`  Failed:  ${report.summary.failed}`);
    console.log(`  Skipped: ${report.summary.skipped}`);
    console.log(`  Output:  ${outputPath}`);

    // Print failures
    const failures = this.results.filter((r) => r.status === 'Failed');
    if (failures.length > 0) {
      console.log(`\n  Failed tests:`);
      for (const f of failures) {
        console.log(`    ✘ ${f.tcId} — ${f.title.replace(/@\S+/g, '').trim()}`);
      }
    }

    console.log(`\n[LambdaTest Reporter] To push results to LambdaTest, ask Kiro:`);
    console.log(`  "Push the lambdatest results to Test Manager"\n`);
  }
}
