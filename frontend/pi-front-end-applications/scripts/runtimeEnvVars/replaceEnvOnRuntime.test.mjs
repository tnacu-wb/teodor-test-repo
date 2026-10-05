import assert from "node:assert/strict";
import { execFile } from "node:child_process";
import { mkdtemp, mkdir, readFile, rm, writeFile } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { promisify } from "node:util";
import test from "node:test";

const run = promisify(execFile);
const cli = fileURLToPath(new URL("./index.mjs", import.meta.url));

for (const placeholder of [
  "WB_PLACEHOLDER_ENV_START_TEST_VALUE_WB_PLACEHOLDER_ENV_END",
  '$(printf INJECTED) `printf INJECTED` "quoted"',
  "--files",
]) {
  test(`replaces the literal placeholder ${JSON.stringify(placeholder)}`, async () => {
    const root = await mkdtemp(path.join(tmpdir(), "runtime-env-args-"));
    try {
      const workingDir = path.join(root, "app with spaces; $(printf INJECTED)");
      await mkdir(workingDir);
      const envFile = path.join(root, ".env.production");
      const target = path.join(workingDir, ".hidden-bundle.js");
      await writeFile(envFile, `TEST_VALUE=${placeholder}`);
      await writeFile(target, `before ${placeholder} after ${placeholder}`);

      await run(process.execPath, [cli, "replace", "--working-dir", workingDir,
        "--env-file-path", envFile], {
        env: { ...process.env, TEST_VALUE: "replacement" },
      });

      assert.equal(await readFile(target, "utf8"), "before replacement after replacement");
    } finally {
      await rm(root, { recursive: true, force: true });
    }
  });
}

test("no matching files remains a successful no-op", async () => {
  const root = await mkdtemp(path.join(tmpdir(), "runtime-env-no-match-"));
  try {
    const workingDir = path.join(root, "app");
    await mkdir(workingDir);
    const envFile = path.join(root, ".env.production");
    const target = path.join(workingDir, "bundle.js");
    await writeFile(envFile, "TEST_VALUE=missing-placeholder");
    await writeFile(target, "unchanged");
    await run(process.execPath, [cli, "replace", "--working-dir", workingDir,
      "--env-file-path", envFile], {
      env: { ...process.env, TEST_VALUE: "replacement" },
    });
    assert.equal(await readFile(target, "utf8"), "unchanged");
  } finally {
    await rm(root, { recursive: true, force: true });
  }
});
