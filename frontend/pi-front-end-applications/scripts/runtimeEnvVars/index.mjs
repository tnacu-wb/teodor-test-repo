#!/usr/bin/env node
import { runGenerateScript } from "./generateEnvFile.mjs";
import { logger } from "./logger.mjs";
import { runReplaceScript } from "./replaceEnvOnRuntime.mjs";
import { DEFAULT_ERROR_EXIT_CODE } from "./consts.mjs";
import { parseArgs } from "node:util";

const COMMANDS = ["generate", "replace"];

const USAGE = `Usage: index.mjs <command> [options]

Commands:
  generate  Generate .env.production file with placeholder values based on next.config.js file
  replace   Replace env variables with runtime values

Options:
  -w, --working-dir     Working directory path where next.config.js file exists   [required]
      --env-file-path   Path to the .env.production file                          [required]
      --env             Alias of --env-file-path
  -v, --verbose         Increase log verbosity; repeat for debug output (-vv)

Examples:
  index.mjs generate --working-dir=./path/to/working/dir --env-file-path=./path/to/env/file
  index.mjs replace --working-dir=./path/to/working/dir --env-file-path=./path/to/env/file`;

const abort = (message) => {
  logger.error(`${message}\n\n${USAGE}`);
  process.exit(DEFAULT_ERROR_EXIT_CODE);
};

let parsed;
try {
  parsed = parseArgs({
    options: {
      "working-dir": { type: "string", short: "w" },
      "env-file-path": { type: "string" },
      env: { type: "string" },
      verbose: { type: "boolean", short: "v", multiple: true },
      help: { type: "boolean", short: "h" },
    },
    allowPositionals: true,
    strict: true,
  });
} catch (error) {
  abort(error.message);
}

const { values, positionals } = parsed;

// `--verbose` is collected as an array so that repeats act as a counter (-v => 1, -vv => 2).
logger.init(values.verbose?.length ?? 0);

if (values.help) {
  logger.warn(USAGE);
  process.exit(0);
}

const [executionCommand] = positionals;
if (!executionCommand) {
  abort("You need at least one command before moving on");
}
if (!COMMANDS.includes(executionCommand)) {
  abort(`Unknown command: ${executionCommand}`);
}

// `--env` is kept as an alias of `--env-file-path` for backwards compatibility.
const workingDir = values["working-dir"];
const envFilePath = values["env-file-path"] ?? values.env;

if (!workingDir) {
  abort("Missing required argument: working-dir");
}
if (!envFilePath) {
  abort("Missing required argument: env-file-path");
}

logger.debug("Using the following arguments: ", { workingDir, envFilePath });
logger.info("Running command:", executionCommand);

switch (executionCommand) {
  case "replace":
    await runReplaceScript(workingDir, envFilePath);
    break;
  case "generate":
    await runGenerateScript(workingDir, envFilePath);
    break;
}

// exit or node will be stuck because of appdynamics
process.exit(0);
