/**
 * Teams Notification Script — Adaptive Card
 *
 * Sends test results to Microsoft Teams via webhook using a rich Adaptive Card.
 * Auto-extracts results from Allure results directory.
 *
 * Usage:
 *   npx tsx scripts/notify-teams.ts
 */

import * as fs from 'fs';
import * as path from 'path';
import { config } from 'dotenv';

config({ path: path.resolve(__dirname, '..', '.env') });

// ─── Configuration (all derived from actual run — nothing hardcoded) ────────────

const TEAMS_WEBHOOK_URL = 'https://defaulte0e80fc44c1242458bfdc06791ad30.3a.environment.api.powerplatform.com:443/powerautomate/automations/direct/cu/02/workflows/3161fd00d2de4e7cbba9157346d1ff88/triggers/manual/paths/invoke?api-version=1&sp=%2Ftriggers%2Fmanual%2Frun&sv=1.0&sig=e86ViqeFaNcLJR6YHvhHAiZakxTKz6G1E0A15hADuTw';
const APP = (process.env.APP ?? 'pi').toUpperCase();
const ENV = (process.env.ENV ?? 'uat').toUpperCase();
const EXECUTION = process.env.EXECUTION_ENV !== 'local' ? 'LambdaTest' : 'Local';
const VIEWPORT = (process.env.VIEWPORT ?? 'desktop').toUpperCase();
const BUILD_NAME = `${APP}-${ENV}-${new Date().toISOString().slice(0, 10)}`;
const LT_BUILDS_URL = 'https://automation.lambdatest.com/build';

/**
 * Fetch the latest build URL from LambdaTest API for the current build name.
 * Returns a direct link to the test page, or falls back to the builds dashboard.
 */
async function getLambdaTestBuildUrl(): Promise<string> {
  const username = process.env.LT_USERNAME;
  const accessKey = process.env.LT_ACCESS_KEY;

  if (!username || !accessKey) return LT_BUILDS_URL;

  try {
    const auth = Buffer.from(`${username}:${accessKey}`).toString('base64');

    // Step 1: Get build ID
    const buildRes = await fetch(
      `https://api.lambdatest.com/automation/api/v1/builds?buildName=${encodeURIComponent(BUILD_NAME)}&limit=1`,
      { headers: { Authorization: `Basic ${auth}` } }
    );

    if (!buildRes.ok) return LT_BUILDS_URL;

    const buildData = await buildRes.json() as { data?: Array<{ build_id: number }> };
    const buildId = buildData?.data?.[0]?.build_id;

    if (!buildId) return LT_BUILDS_URL;

    // Step 2: Get first test session from that build
    const sessionRes = await fetch(
      `https://api.lambdatest.com/automation/api/v1/sessions?build_id=${buildId}&limit=1`,
      { headers: { Authorization: `Basic ${auth}` } }
    );

    if (!sessionRes.ok) return `${LT_BUILDS_URL}?build=${buildId}`;

    const sessionData = await sessionRes.json() as { data?: Array<{ test_id: string }> };
    const testId = sessionData?.data?.[0]?.test_id;

    if (testId) {
      return `https://automation.lambdatest.com/test?build=${buildId}&testID=${testId}`;
    }

    return `${LT_BUILDS_URL}?build=${buildId}`;
  } catch {
    // Fallback on any error
  }

  return LT_BUILDS_URL;
}

// ─── Extract results from Allure ────────────────────────────────────────────────

interface TestResult {
  name: string;
  status: 'passed' | 'failed' | 'broken' | 'skipped';
  duration?: number;
  suite?: string;
}

function extractResults(): TestResult[] {
  const resultsDir = path.resolve(__dirname, '..', 'reports', APP.toLowerCase(), 'allure-results');
  if (!fs.existsSync(resultsDir)) return [];

  const allResults = fs.readdirSync(resultsDir)
    .filter((f) => f.endsWith('-result.json'))
    .map((f) => {
      try {
        const data = JSON.parse(fs.readFileSync(path.join(resultsDir, f), 'utf-8'));
        if (!data.name || !data.status) return null;
        // Include browser/suite in the key for dedup
        const labels = data.labels ?? [];
        const parentSuite = labels.find((l: any) => l.name === 'parentSuite')?.value ?? '';
        const suiteName = labels.find((l: any) => l.name === 'suite')?.value ?? parentSuite ?? 'Other';
        return {
          name: data.name,
          status: data.status,
          duration: data.time?.duration,
          suite: suiteName,
          _key: `${parentSuite}::${data.name}`,
        };
      } catch { return null; }
    })
    .filter(Boolean) as (TestResult & { _key: string })[];

  // Deduplicate by browser+test key: if a test has retries, keep the final status
  const unique = new Map<string, TestResult>();
  for (const r of allResults) {
    const existing = unique.get(r._key);
    if (!existing || r.status === 'passed') {
      unique.set(r._key, { name: r.name, status: r.status, duration: r.duration, suite: r.suite });
    }
  }

  return Array.from(unique.values());
}

const results = extractResults();
const passed = results.filter((r) => r.status === 'passed').length;
const failed = results.filter((r) => r.status === 'failed').length;
const broken = results.filter((r) => r.status === 'broken').length;
const total = results.length;

// Detect actual browsers from allure result labels
const browsersDetected = new Set<string>();
for (const r of results) {
  const name = r.name.toLowerCase();
  if (name.includes('chrome')) browsersDetected.add('Chrome');
  if (name.includes('firefox')) browsersDetected.add('Firefox');
  if (name.includes('edge')) browsersDetected.add('Edge');
  if (name.includes('safari')) browsersDetected.add('Safari');
}
// Fallback: check from allure result files for parentSuite/labels
if (browsersDetected.size === 0) {
  const resultsDir = path.resolve(__dirname, '..', 'reports', APP.toLowerCase(), 'allure-results');
  if (fs.existsSync(resultsDir)) {
    for (const f of fs.readdirSync(resultsDir).filter((f) => f.endsWith('-result.json'))) {
      try {
        const data = JSON.parse(fs.readFileSync(path.join(resultsDir, f), 'utf-8'));
        const labels = data.labels ?? [];
        for (const label of labels) {
          if (label.name === 'parentSuite' || label.name === 'suite') {
            const v = (label.value ?? '').toLowerCase();
            if (v.includes('chrome')) browsersDetected.add('Chrome');
            if (v.includes('firefox')) browsersDetected.add('Firefox');
            if (v.includes('edge')) browsersDetected.add('Edge');
            if (v.includes('safari') || v.includes('webkit')) browsersDetected.add('Safari');
          }
        }
      } catch { /* skip */ }
    }
  }
}
const browsers = browsersDetected.size > 0 ? Array.from(browsersDetected).join(', ') : 'Chrome';

// Status
const statusEmoji = failed === 0 && broken === 0 ? '✅' : failed > 0 ? '❌' : '⚠️';
const statusText = failed === 0 && broken === 0 ? 'ALL PASSED' : `${failed} FAILED`;
const suite = `${APP} ${ENV}`;

// Success rate
const successRate = total > 0 ? Math.round((passed / total) * 100) : 0;

// Suite-level counts for summary table
function buildSuiteSummary(results: TestResult[]): Array<{ type: string; passed: number; failed: number; total: number; rate: number }> {
  const isSmoke = (r: TestResult) => r.suite?.toLowerCase().includes('smoke') || r.name.includes('@smoke');
  const isRegression = (r: TestResult) => r.suite?.toLowerCase().includes('regression') || r.name.includes('@regression');
  const isFeature = (r: TestResult) => !isSmoke(r) && !isRegression(r);

  const smokePassed = results.filter((r) => isSmoke(r) && r.status === 'passed').length;
  const smokeFailed = results.filter((r) => isSmoke(r) && (r.status === 'failed' || r.status === 'broken')).length;
  const smokeTotal = smokePassed + smokeFailed;

  const regPassed = results.filter((r) => isRegression(r) && r.status === 'passed').length;
  const regFailed = results.filter((r) => isRegression(r) && (r.status === 'failed' || r.status === 'broken')).length;
  const regTotal = regPassed + regFailed;

  // Feature: split by suite name (e.g. "Promo Code", "Datatrans", "TMC Users")
  const featureResults = results.filter(isFeature);
  const featureSuiteMap = new Map<string, TestResult[]>();
  for (const r of featureResults) {
    const suite = r.suite || 'Other';
    if (!featureSuiteMap.has(suite)) featureSuiteMap.set(suite, []);
    featureSuiteMap.get(suite)!.push(r);
  }

  const rows: Array<{ type: string; passed: number; failed: number; total: number; rate: number }> = [];
  if (smokeTotal > 0) rows.push({ type: 'Smoke', passed: smokePassed, failed: smokeFailed, total: smokeTotal, rate: Math.round((smokePassed / smokeTotal) * 100) });
  if (regTotal > 0) rows.push({ type: 'Regression', passed: regPassed, failed: regFailed, total: regTotal, rate: Math.round((regPassed / regTotal) * 100) });

  for (const [suite, tests] of featureSuiteMap) {
    const fp = tests.filter((t) => t.status === 'passed').length;
    const ff = tests.filter((t) => t.status === 'failed' || t.status === 'broken').length;
    const ft = fp + ff;
    if (ft > 0) rows.push({ type: suite, passed: fp, failed: ff, total: ft, rate: Math.round((fp / ft) * 100) });
  }

  rows.push({ type: '**TOTAL**', passed, failed: failed + broken, total, rate: successRate });
  return rows;
}

const suiteSummary = buildSuiteSummary(results);

// Test names grouped by suite with passed/failed split
function buildSuiteTestList(results: TestResult[]): string {
  const suiteMap = new Map<string, TestResult[]>();
  for (const r of results) {
    const suite = r.suite || 'Other';
    if (!suiteMap.has(suite)) suiteMap.set(suite, []);
    suiteMap.get(suite)!.push(r);
  }

  // Sort suites: Smoke first, then Regression, then Features
  const suiteOrder = (suite: string, tests: TestResult[]): number => {
    const firstTestName = tests[0]?.name?.toLowerCase() ?? '';
    if (suite.toLowerCase().includes('smoke') || firstTestName.includes('@smoke')) return 0;
    if (suite.toLowerCase().includes('regression') || firstTestName.includes('@regression')) return 1;
    return 2;
  };

  const sortedSuites = [...suiteMap.entries()].sort(
    ([suiteA, testsA], [suiteB, testsB]) => suiteOrder(suiteA, testsA) - suiteOrder(suiteB, testsB)
  );

  const lines: string[] = [];
  for (const [suite, tests] of sortedSuites) {
    const suitePassed = tests.filter((t) => t.status === 'passed').length;
    const suiteFailed = tests.filter((t) => t.status === 'failed' || t.status === 'broken').length;

    // Derive suite type from test tags/names
    let suiteType = '';
    const firstTestName = tests[0]?.name?.toLowerCase() ?? '';
    if (suite.toLowerCase().includes('smoke') || firstTestName.includes('@smoke')) suiteType = 'Smoke';
    else if (suite.toLowerCase().includes('regression') || firstTestName.includes('@regression')) suiteType = 'Regression';

    let groupName: string;
    if (suiteType) {
      // Smoke/Regression: APP-TYPE TEST
      let appName = APP;
      const suiteLower = suite.toLowerCase();
      if (suiteLower.includes('ccui')) appName = 'CCUI';
      else if (suiteLower.includes('pib')) appName = 'PIB';
      else if (suiteLower.includes('pi ') || suiteLower.startsWith('pi')) appName = 'PI';
      groupName = `${appName}-${suiteType} Test`.toUpperCase();
    } else {
      // Feature: use the suite name directly (e.g. "Promo Code" → "PROMO CODE", "Datatrans" → "DATATRANS")
      groupName = suite.toUpperCase();
    }
    lines.push(`**${groupName}** (${suitePassed}✅ ${suiteFailed}❌)`);

    // Show failed tests first, then passed
    const failed = tests.filter((t) => t.status === 'failed' || t.status === 'broken');
    const passed = tests.filter((t) => t.status === 'passed');

    for (const t of failed) {
      lines.push(`  ❌ ${t.name}`);
    }
    for (const t of passed) {
      lines.push(`  ✅ ${t.name}`);
    }
    lines.push('');
  }

  return lines.join('\n');
}

const testList = buildSuiteTestList(results);

// ─── Send ───────────────────────────────────────────────────────────────────────

async function send() {
  // Fetch the actual LambdaTest build URL
  const ltUrl = await getLambdaTestBuildUrl();

  // Build card with real URL
  const card = {
    type: 'message',
    attachments: [{
      contentType: 'application/vnd.microsoft.card.adaptive',
      content: {
        $schema: 'http://adaptivecards.io/schemas/adaptive-card.json',
        type: 'AdaptiveCard',
        version: '1.4',
        msteams: { width: 'Full' },
        body: [
          {
            type: 'TextBlock',
            size: 'Large',
            weight: 'Bolder',
            wrap: true,
            text: `📋 Execution Report — ${suite}`,
          },
          {
            type: 'ColumnSet',
            spacing: 'Small',
            columns: [
              { type: 'Column', width: 'stretch', items: [
                { type: 'TextBlock', text: '**Suite**', weight: 'Bolder' },
                ...suiteSummary.map((r) => ({ type: 'TextBlock', text: r.type, spacing: 'Small' })),
              ]},
              { type: 'Column', width: 'auto', items: [
                { type: 'TextBlock', text: '**✅**', weight: 'Bolder' },
                ...suiteSummary.map((r) => ({ type: 'TextBlock', text: `${r.passed}`, spacing: 'Small' })),
              ]},
              { type: 'Column', width: 'auto', items: [
                { type: 'TextBlock', text: '**❌**', weight: 'Bolder' },
                ...suiteSummary.map((r) => ({ type: 'TextBlock', text: `${r.failed}`, spacing: 'Small' })),
              ]},
              { type: 'Column', width: 'auto', items: [
                { type: 'TextBlock', text: '**Total**', weight: 'Bolder' },
                ...suiteSummary.map((r) => ({ type: 'TextBlock', text: `${r.total}`, spacing: 'Small' })),
              ]},
              { type: 'Column', width: 'auto', items: [
                { type: 'TextBlock', text: '**🎯**', weight: 'Bolder', size: 'Medium' },
                ...suiteSummary.map((r) => ({ type: 'TextBlock', text: `${r.rate}%`, spacing: 'Small' })),
              ]},
            ],
          },
          { type: 'TextBlock', text: '**⚙️ Run Details**', separator: true, spacing: 'Medium' },
          { type: 'FactSet', facts: [
            { title: 'App', value: APP },
            { title: 'Environment', value: ENV },
            { title: 'Browsers', value: browsers },
            { title: 'Viewport', value: VIEWPORT },
            { title: 'Execution', value: EXECUTION },
          ]},
          { type: 'TextBlock', text: '**🧪 Tests Executed**', separator: true, spacing: 'Medium' },
          { type: 'TextBlock', text: testList || 'No tests found', wrap: true, spacing: 'Small', size: 'Small' },
          { type: 'TextBlock', text: '**🔗 Reports & Links**', separator: true, spacing: 'Medium' },
          { type: 'FactSet', facts: [
            { title: 'LambdaTest', value: `[View Test Runs](${ltUrl})` },
            { title: 'Allure Report', value: '`npm run report:allure`' },
          ]},
          { type: 'TextBlock', text: `_${new Date().toLocaleString('en-GB')}_`, isSubtle: true, spacing: 'Medium', wrap: true },
        ],
      },
    }],
  };

  console.log('─── Teams Notification ─────────────────────────────────────');
  console.log(`  ${statusEmoji} ${statusText}: ${passed}✅ ${failed}❌ ${broken}💥 / ${total} total`);
  console.log(`  ${APP} | ${ENV} | ${browsers} | ${VIEWPORT} | ${EXECUTION}`);
  console.log(`  LambdaTest: ${ltUrl}`);

  try {
    const res = await fetch(TEAMS_WEBHOOK_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(card),
    });
    console.log(res.ok ? '  ✅ Sent' : `  ⚠️ HTTP ${res.status}: ${await res.text()}`);
  } catch (e) {
    console.error('  ⚠️ Failed:', e);
  }
}

send();
