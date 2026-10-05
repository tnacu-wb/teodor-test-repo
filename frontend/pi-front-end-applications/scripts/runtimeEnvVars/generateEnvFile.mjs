import {
  DEFAULT_ERROR_EXIT_CODE,
  PLACEHOLDER_ENV_END,
  PLACEHOLDER_ENV_START,
} from "./consts.mjs";
import { logger } from "./logger.mjs";
import fs from "fs";
import path from 'path';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);

const checkWorkingDirExistsOrAbort = (workingDir) => {
  if (!fs.existsSync(workingDir)) {
    logger.warn(
      `Working directory ${workingDir} does not exist, terminating execution`
    );
    process.exit(DEFAULT_ERROR_EXIT_CODE);
  } else {
    logger.debug(`Using working directory: ${workingDir}`);
  }
};

const checkEnvFileExistsOrWarn = (envFile) => {
  if (fs.existsSync(envFile)) {
    logger.warn(
      `Env file ${envFile} already exists, overwriting it's contetns`
    );
  } else {
    logger.debug(`Env file ${envFile} does not exist, creating it`);
  }
};

const writeEnvFile = (workingDir, envFile) => {
  const nextConfigPath = `${workingDir}next.config.js`;
  logger.debug(`Processing next.config file: ${nextConfigPath}`);

  const nextConfig = require(path.resolve(process.cwd(), nextConfigPath));

  const serverKeys = Object.keys(nextConfig.serverRuntimeConfig);
  const publicKeys = Object.keys(nextConfig.publicRuntimeConfig);

  //add placeholders for environment values
  const keys = [... new Set(serverKeys.concat(serverKeys, publicKeys))]
    .map(key => `${key}=${PLACEHOLDER_ENV_START}${key}${PLACEHOLDER_ENV_END}`)
    .join('\n');

  fs.writeFileSync(envFile, keys);

  logger.debug(`Generated env file: ${envFile}`);
};

// Main function for generating .env.production file
export const runGenerateScript = async (workingDir, envFile) => {
  checkWorkingDirExistsOrAbort(workingDir);
  checkEnvFileExistsOrWarn(envFile);

  try {
    writeEnvFile(workingDir, envFile);
  } catch (error) {
    logger.error(
      "Something went wrong while processing or writting the values files: ",
      error
    );
    process.exit(DEFAULT_ERROR_EXIT_CODE);
  }
};
