#!/usr/bin/env node

/// <reference types="node" />

/**
 * Compare Playwright Test Case IDs with Zephyr CSV/XLSX Export
 *
 * This script compares test case IDs found in Playwright spec files, legacy WebdriverIO spec files,
 * or Karate feature files (in "test"/"it" functions or "Scenario" test names containing
 * "TestCase ID: <id1>,<id2>") with test case IDs from a Zephyr CSV or XLSX export file.
 * Use this Zephyr query to export the needed data:
 * for FE test cases: '(name ~ "[CCUI]" or name ~ "[PI]" or name ~ "[BB]" or name ~ "[IB]" or tag = ("distr") ) and name !~ "[REST]" and name !~ "[DISTR]" and name !~ "GraphQL" and name !~ "[KIOSK]" and tag = ("needed_for_regression") and automated = true'
 * for MS test cases: '(name ~ "[REST]" or name ~ "GraphQL") and ext-automation-priority != ("3","4") and tag != ("baseline") and tag = ("needed_for_regression") and automated = true'
 *
 * Usage:
 *   npx tsx scripts/compare-test-cases.ts --specs <specs_folder> (--csv <zephyr_csv> | --xlsx <zephyr_xlsx>) [options]
 *   npx tsx scripts/compare-test-cases.ts -s <specs_folder> -c <zephyr_csv> [options]
 *   npx tsx scripts/compare-test-cases.ts -s <specs_folder> -x <zephyr_xlsx> [options]
 *
 * Options:
 *   --specs, -s     Path to the folder containing spec files (required)
 *   --csv, -c       Path to the CSV file with Test case Name and Test case ID columns (conflicts with --xlsx)
 *   --xlsx, -x      Path to the XLSX file with Test case Name and Test case ID columns (conflicts with --csv)
 *   --show, -f      Filter results: code, csv, both, notboth, or all (default: all)
 *   --html          Generate an HTML report (provide output file path)
 *   --help          Show help
 *
 * Filter options:
 *   code     - Show only IDs in code but not in CSV/XLSX
 *   csv      - Show only IDs in CSV/XLSX but not in code
 *   both     - Show only IDs present in both code and CSV/XLSX
 *   notboth  - Show only IDs that are not in both (only in code OR only in CSV/XLSX)
 *   all      - Show all categories (default)
 *
 * Examples:
 *   npx tsx scripts/compare-test-cases.ts --specs ./tests --csv zephyr-export.csv
 *   npx tsx scripts/compare-test-cases.ts --specs ./tests --xlsx zephyr-export.xlsx
 *   npx tsx scripts/compare-test-cases.ts -s ./tests -c zephyr.csv --show both
 *   npx tsx scripts/compare-test-cases.ts -s ./tests -x zephyr.xlsx -f notboth
 *   npx tsx scripts/compare-test-cases.ts -s ./tests -x zephyr.xlsx --html report.html
 */

import * as fs from 'fs';
import * as path from 'path';
import * as readline from 'readline';
import readXlsxFile from 'read-excel-file/node';

const SHOW_FILTERS = ['code', 'csv', 'both', 'notboth', 'all'] as const;

type ShowFilter = typeof SHOW_FILTERS[number];

interface CliOptions {
  specs: string;
  csv?: string;
  xlsx?: string;
  show: ShowFilter;
  html?: string;
}

interface TestInfo {
  testName: string;
  fileName: string;
  filePath: string;
}

interface CodeOnlyItem {
  id: string;
  tests: TestInfo[];
}

const colors = {
  red: '\u001b[91m',
  green: '\u001b[92m',
  yellow: '\u001b[93m',
  blue: '\u001b[94m',
  cyan: '\u001b[96m',
  gray: '\u001b[90m',
  bold: '\u001b[1m',
  bgRed: '\u001b[41m\u001b[37m',
  bgGreen: '\u001b[42m\u001b[37m',
  bgBlue: '\u001b[44m\u001b[37m',
  reset: '\u001b[0m',
};

function color(text: string, ansi: string): string {
  return `${ansi}${text}${colors.reset}`;
}

function usage(): string {
  return `Usage: npx tsx scripts/compare-test-cases.ts --specs <specs_folder> (--csv <zephyr_csv> | --xlsx <zephyr_xlsx>) [options]\n\n` +
    `Options:\n` +
    `  --specs, -s     Path to the folder containing spec files (required)\n` +
    `  --csv, -c       Path to the CSV file with Test case Name and Test case ID columns (conflicts with --xlsx)\n` +
    `  --xlsx, -x      Path to the XLSX file with Test case Name and Test case ID columns (conflicts with --csv)\n` +
    `  --show, -f      Filter results: code, csv, both, notboth, or all (default: all)\n` +
    `  --html          Generate an HTML report (provide output file path)\n` +
    `  --help          Show help`;
}

function parseArgs(args: string[]): CliOptions {
  const options: Partial<CliOptions> = { show: 'all' };

  for (let index = 0; index < args.length; index++) {
    const arg = args[index];
    const next = args[index + 1];

    switch (arg) {
      case '--help':
        console.log(usage());
        process.exit(0);
      case '--specs':
      case '-s':
        options.specs = readOptionValue(arg, next);
        index++;
        break;
      case '--csv':
      case '-c':
        options.csv = readOptionValue(arg, next);
        index++;
        break;
      case '--xlsx':
      case '-x':
        options.xlsx = readOptionValue(arg, next);
        index++;
        break;
      case '--show':
      case '-f':
        options.show = parseShowFilter(readOptionValue(arg, next));
        index++;
        break;
      case '--html':
        options.html = readOptionValue(arg, next);
        index++;
        break;
      default:
        throw new Error(`Unknown option: ${arg}`);
    }
  }

  if (!options.specs) {
    throw new Error('You must provide --specs');
  }
  if (!options.csv && !options.xlsx) {
    throw new Error('You must provide either --csv or --xlsx');
  }
  if (options.csv && options.xlsx) {
    throw new Error('--csv conflicts with --xlsx');
  }

  return options as CliOptions;
}

function readOptionValue(option: string, value?: string): string {
  if (!value || value.startsWith('-')) {
    throw new Error(`${option} requires a value`);
  }
  return value;
}

function parseShowFilter(value: string): ShowFilter {
  if (SHOW_FILTERS.includes(value as ShowFilter)) {
    return value as ShowFilter;
  }
  throw new Error(`--show must be one of: ${SHOW_FILTERS.join(', ')}`);
}

/**
 * Recursively finds JavaScript, TypeScript, and Karate feature files in a directory.
 * @param dir - Directory path to search.
 * @param fileList - Accumulator array for file paths.
 * @returns Array of absolute file paths.
 */
function getAllSpecFiles(dir: string, fileList: string[] = []): string[] {
  const files = fs.readdirSync(dir);
  files.forEach((file) => {
    const filePath = path.join(dir, file);
    if (fs.statSync(filePath).isDirectory()) {
      getAllSpecFiles(filePath, fileList);
    } else if (file.endsWith('.js') || file.endsWith('.ts') || file.endsWith('.feature')) {
      fileList.push(filePath);
    }
  });
  return fileList;
}

/**
 * Extracts test case IDs from Playwright/WebdriverIO spec files and Karate feature files.
 * Searches for patterns like: test("... TestCase ID: 123,456 ...") or it("... TestCase ID: 123,456 ...").
 * @param specFiles - Array of file paths to search.
 * @returns Map of test case IDs to test info objects (testName, fileName, filePath).
 */
function extractTestCaseIdsFromSpecs(specFiles: string[]): Map<string, TestInfo[]> {
  const testCaseIdRegex = /(?:test|it)(?:\.(?:skip|only|fixme))?\s*\(\s*([`'"])([\s\S]*?TestCase ID:\s*([0-9, ]+)[\s\S]*?)\1|Scenario(?: Outline)?:[\s\S]*?TestCase ID:\s*([0-9, ]+)/gs;
  const idMap = new Map<string, TestInfo[]>();

  for (const file of specFiles) {
    const content = fs.readFileSync(file, 'utf8');
    let match: RegExpExecArray | null;

    while ((match = testCaseIdRegex.exec(content)) !== null) {
      if (match[2] && match[3]) {
        addIdsToMap(idMap, match[3], {
          testName: match[2].trim(),
          fileName: path.basename(file),
          filePath: file,
        });
      }

      if (match[4]) {
        const scenarioMatch = /Scenario(?: Outline)?:\s*([^\n\r]*)/i.exec(content.slice(Math.max(0, match.index - 100), match.index + 100));
        addIdsToMap(idMap, match[4], {
          testName: scenarioMatch ? scenarioMatch[1].trim() : 'Scenario',
          fileName: path.basename(file),
          filePath: file,
        });
      }
    }
  }

  return idMap;
}

function addIdsToMap(idMap: Map<string, TestInfo[]>, idsString: string, testInfo: TestInfo): void {
  for (const id of idsString.split(',')) {
    const trimmedId = id.trim();
    if (!trimmedId) continue;

    const tests = idMap.get(trimmedId) ?? [];
    tests.push(testInfo);
    idMap.set(trimmedId, tests);
  }
}

/**
 * Extracts test case IDs from a CSV file with Test case ID column.
 * @param csvPath - Path to the CSV file.
 * @returns Promise resolving to set of test case IDs from CSV.
 */
async function extractTestCaseIdsFromCSV(csvPath: string): Promise<Set<string>> {
  return new Promise((resolve, reject) => {
    const idSet = new Set<string>();
    let testCaseIdIndex = -1;
    let isFirstLine = true;
    const rl = readline.createInterface({
      input: fs.createReadStream(csvPath),
      crlfDelay: Infinity,
    });

    rl.on('line', (line) => {
      const columns = parseCSVLine(line);
      if (isFirstLine) {
        const header = columns.map((headerName) => headerName.trim().toLowerCase());
        testCaseIdIndex = header.findIndex((headerName) => headerName === 'test case id' || headerName === 'testcaseid');
        isFirstLine = false;
        return;
      }

      if (testCaseIdIndex === -1) return;

      const id = columns[testCaseIdIndex]?.trim();
      if (id) idSet.add(id);
    });

    rl.on('close', () => resolve(idSet));
    rl.on('error', reject);
  });
}

function parseCSVLine(line: string): string[] {
  const columns: string[] = [];
  let current = '';
  let isQuoted = false;

  for (let index = 0; index < line.length; index++) {
    const character = line[index];
    const next = line[index + 1];

    if (character === '"' && isQuoted && next === '"') {
      current += '"';
      index++;
    } else if (character === '"') {
      isQuoted = !isQuoted;
    } else if (character === ',' && !isQuoted) {
      columns.push(current);
      current = '';
    } else {
      current += character;
    }
  }

  columns.push(current);
  return columns;
}

/**
 * Extracts test case IDs from an XLSX file with Test case ID column.
 * @param xlsxPath - Path to the XLSX file.
 * @returns Promise resolving to set of test case IDs from XLSX.
 */
async function extractTestCaseIdsFromXLSX(xlsxPath: string): Promise<Set<string>> {
  const idSet = new Set<string>();
  const rows = normalizeXlsxRows(await readXlsxFile(xlsxPath));
  if (!rows.length) return idSet;

  const header = rows[0].map((headerName) => String(headerName ?? '').trim().toLowerCase());
  const testCaseIdIndex = header.findIndex((headerName) => headerName === 'test case id' || headerName === 'testcaseid');
  if (testCaseIdIndex === -1) return idSet;

  for (let index = 1; index < rows.length; index++) {
    const row = rows[index];
    const id = String(row[testCaseIdIndex] ?? '').trim();
    if (id) idSet.add(id);
  }

  return idSet;
}

function normalizeXlsxRows(result: unknown): unknown[][] {
  if (!Array.isArray(result)) return [];
  if (Array.isArray(result[0])) return result as unknown[][];

  const firstSheet = result[0];
  if (firstSheet && typeof firstSheet === 'object' && 'data' in firstSheet) {
    const rows = (firstSheet as { data?: unknown }).data;
    if (Array.isArray(rows)) return rows as unknown[][];
  }

  return [];
}

function htmlSection<T>(title: string, sectionColor: string, items: T[], renderItem: (item: T) => string): string {
  return `<h2 style="background:${sectionColor};color:#fff;padding:6px;">${title} (${items.length})</h2>\n` +
    (items.length === 0 ? '<p style="color:gray">None found.</p>' : items.map(renderItem).join('\n'));
}

function htmlTestItem(item: CodeOnlyItem, itemColor: string): string {
  return `<div style="margin-bottom:10px;"><b style="color:${itemColor}">TestCase ID:</b> <b>${item.id}</b><ul>` +
    item.tests.map((test) => `<li><span style="color:#e6b800">${escapeHtml(test.testName)}</span> <span style="color:#0bb">(${escapeHtml(test.fileName)})</span></li>`).join('') +
    '</ul></div>';
}

function htmlIdList(item: string, itemColor: string): string {
  return `<div style="margin-bottom:10px;"><span style="color:${itemColor}">${escapeHtml(item)}</span></div>`;
}

function escapeHtml(value: string): string {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

function writeHtmlReport(htmlReport: string, show: ShowFilter, inCodeNotInCSV: CodeOnlyItem[], inCSVNotInCode: string[], inBoth: CodeOnlyItem[]): void {
  let html = '<!DOCTYPE html><html><head><meta charset="utf-8"><title>TestCase Comparison Report</title></head><body style="font-family:sans-serif;">';
  html += '<h1>TestCase Comparison Report</h1>';

  if (show === 'code' || show === 'all' || show === 'notboth') {
    html += htmlSection('TestCase IDs in code but not in CSV/XLSX', '#c0392b', inCodeNotInCSV, (item) => htmlTestItem(item, '#c0392b'));
  }
  if (show === 'csv' || show === 'all' || show === 'notboth') {
    html += htmlSection('TestCase IDs in CSV/XLSX but not in code', '#2980b9', inCSVNotInCode, (item) => htmlIdList(item, '#2980b9'));
  }
  if (show === 'both' || show === 'all') {
    html += htmlSection('TestCase IDs present in both code and CSV/XLSX', '#27ae60', inBoth, (item) => htmlTestItem(item, '#27ae60'));
  }

  html += '</body></html>';
  fs.writeFileSync(htmlReport, html);
  console.log(`\nHTML report generated at: ${htmlReport}`);
}

function printResults(show: ShowFilter, inCodeNotInCSV: CodeOnlyItem[], inCSVNotInCode: string[], inBoth: CodeOnlyItem[]): void {
  if (show === 'code' || show === 'all' || show === 'notboth') {
    console.log(color(`\n=== TestCase IDs in code but not in CSV/XLSX (${inCodeNotInCSV.length}) ===`, colors.bgRed));
    if (inCodeNotInCSV.length === 0) {
      console.log(color('None found.', colors.gray));
    } else {
      inCodeNotInCSV.forEach((item) => {
        console.log(`\n${color('TestCase ID:', colors.red)} ${color(item.id, colors.bold)}`);
        item.tests.forEach((test) => {
          console.log(`  - Test: ${color(`"${test.testName}"`, colors.yellow)}`);
          console.log(`    File: ${color(test.fileName, colors.cyan)}`);
        });
      });
    }
  }

  if (show === 'csv' || show === 'all' || show === 'notboth') {
    console.log(color(`\n=== TestCase IDs in CSV/XLSX but not in code (${inCSVNotInCode.length}) ===`, colors.bgBlue));
    if (inCSVNotInCode.length === 0) {
      console.log(color('None found.', colors.gray));
    } else {
      console.log(color(inCSVNotInCode.join(', '), colors.blue));
    }
  }

  if (show === 'both' || show === 'all') {
    console.log(color(`\n=== TestCase IDs present in both code and CSV/XLSX (${inBoth.length}) ===`, colors.bgGreen));
    if (inBoth.length === 0) {
      console.log(color('None found.', colors.gray));
    } else {
      inBoth.forEach((item) => {
        console.log(`\n${color('TestCase ID:', colors.green)} ${color(item.id, colors.bold)}`);
        item.tests.forEach((test) => {
          console.log(`  - Test: ${color(`"${test.testName}"`, colors.yellow)}`);
          console.log(`    File: ${color(test.fileName, colors.cyan)}`);
        });
      });
    }
  }
}

async function main(): Promise<void> {
  const argv = parseArgs(process.argv.slice(2));
  const specFiles = getAllSpecFiles(argv.specs);
  const codeIdMap = extractTestCaseIdsFromSpecs(specFiles);
  const codeIds = Array.from(codeIdMap.keys());
  const zephyrIds = argv.csv ? await extractTestCaseIdsFromCSV(argv.csv) : await extractTestCaseIdsFromXLSX(argv.xlsx!);

  const inCodeNotInCSV: CodeOnlyItem[] = [];
  for (const id of codeIds) {
    if (!zephyrIds.has(id)) {
      inCodeNotInCSV.push({ id, tests: codeIdMap.get(id) ?? [] });
    }
  }

  const inCSVNotInCode: string[] = [];
  for (const id of zephyrIds) {
    if (!codeIdMap.has(id)) inCSVNotInCode.push(id);
  }

  const inBoth: CodeOnlyItem[] = [];
  for (const id of codeIds) {
    if (zephyrIds.has(id)) {
      inBoth.push({ id, tests: codeIdMap.get(id) ?? [] });
    }
  }

  if (argv.html) {
    writeHtmlReport(argv.html, argv.show, inCodeNotInCSV, inCSVNotInCode, inBoth);
  }

  printResults(argv.show, inCodeNotInCSV, inCSVNotInCode, inBoth);
}

main().catch((error: unknown) => {
  console.error(error instanceof Error ? error.message : error);
  process.exit(1);
});