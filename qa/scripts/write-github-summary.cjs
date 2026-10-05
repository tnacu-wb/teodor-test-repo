const fs = require('node:fs');
const path = require('node:path');

const app = process.env.APP;
const reportPath = path.join('reports', app, 'playwright-results.json');
const summaryPath = process.env.GITHUB_STEP_SUMMARY;

if (!summaryPath) {
  console.error('GITHUB_STEP_SUMMARY is not set; skipping Playwright summary.');
  process.exit(0);
}

if (!fs.existsSync(reportPath)) {
  fs.appendFileSync(summaryPath, '## Playwright results\n\nNo JSON report was produced. Check the test step for setup or execution errors.\n');
  process.exit(0);
}

const report = JSON.parse(fs.readFileSync(reportPath, 'utf8'));
const stats = report.stats ?? {};
const failures = [];

function collectFailures(suites) {
  for (const suite of suites ?? []) {
    for (const spec of suite.specs ?? []) {
      for (const test of spec.tests ?? []) {
        if (test.outcome === 'unexpected') {
          failures.push(`${spec.title}${test.projectName ? ` (${test.projectName})` : ''}`);
        }
      }
    }
    collectFailures(suite.suites);
  }
}

collectFailures(report.suites);

const lines = [
  '## Playwright results',
  '',
  '| Passed | Failed | Flaky | Skipped |',
  '| ---: | ---: | ---: | ---: |',
  `| ${stats.expected ?? 0} | ${stats.unexpected ?? 0} | ${stats.flaky ?? 0} | ${stats.skipped ?? 0} |`,
];

if (failures.length > 0) {
  lines.push('', '### Failed tests', '', ...failures.map((title) => `- ${title.replace(/[\r\n]/g, ' ')}`));
}

fs.appendFileSync(summaryPath, `${lines.join('\n')}\n`);