import { DEFAULT_ERROR_EXIT_CODE } from "./consts.mjs";
import { logger } from "./logger.mjs";
import { pipeAsync } from "./pipeAsync.mjs";
import { execFile } from "node:child_process";
import fs, { promises } from "node:fs";
import { promisify } from "node:util";
import path from "node:path";

// `execFile` runs ripgrep directly instead of through a shell, so placeholder values
// containing shell metacharacters cannot be interpreted as commands.
const execFilePromise = promisify(execFile);

// The standalone bundle can contain a placeholder in thousands of files; the default
// 1 MB stdout buffer would truncate that list and silently skip replacements.
const RIPGREP_MAX_BUFFER = 64 * 1024 * 1024;

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

const checkEnvFileExistsOrAbort = (envFile) => {
  if (!fs.existsSync(envFile)) {
    logger.warn(`Env file ${envFile} not found, terminating execution`);
    process.exit(DEFAULT_ERROR_EXIT_CODE);
  } else {
    logger.debug(`Env file ${envFile} does not exist, creating it`);
  }
};

/**
 *
 * @param {string} envFilePath Path to .env file
 * @returns {Promise<string>} - .env file content
 */
const readDotFile = async (envFilePath) => {
  try {
    return await promises.readFile(envFilePath, "utf8");
  } catch (error) {
    logger.error(`Something went wrong while reading ${envFilePath}`, error);
    process.exit(DEFAULT_ERROR_EXIT_CODE);
  }
};

/**
 * Generate array of objects containing variable name and placeholder value from any .env file content
 * @param {string} fileContent - .env File content
 * @returns {Array<{variableName: string, placeholderValue: string}>} - Array of objects containing variable name and placeholder value
 */
const generateEnvVarsObjectFromDotFile = (fileContent) => {
  return (
    fileContent
      .split("\n")
      // Remove empty lines
      .filter((line) => {
        return line.trim().length > 0;
      })
      // Remove comments
      .filter((line) => {
        return !line.startsWith("#");
      })
      // Remove lines without =
      .filter((line) => {
        return line.includes("=");
      })
      .map((line) => {
        const [variableName, placeholderValue] = line.split("=");
        return {
          variableName,
          placeholderValue,
        };
      })
  );
};

/**
 * Append runtime values to envVarObjects
 * @param {Array<Object<{variableName: string placeholderValue: string}>>} envVarObjects
 * @returns {Array<Object<{variableName: string placeholderValue: string, runtimeValue: string}>>} - Array of objects containing variable name, placeholder value and runtime value
 */
const addRuntimeEnvValues = (envVarObjects) => {
  return envVarObjects.map((envObj) => {
    const runtimeValue = process.env[envObj.variableName];
    if (!runtimeValue) {
      logger.debug(`No runtime value found for ${envObj.variableName}`);
    }
    return {
      ...envObj,
      runtimeValue: runtimeValue || "",
    };
  });
};

/**
 * Run RipGrep to get the list of all existing files containing searchWord
 * This is an exact match search
 * @param {string} workingDir path to folder where to search for files
 * @param {string} searchWord string to search for
 * @returns {Array<strings>} - Array of file paths containing searchWord
 */
const getFilesCotainingString = async (workingDir, searchWord) => {
  try {
    const filesContainingWord = await execFilePromise(
      "rg",
      ["--hidden", "-F", "-l", "-uuu", "--", searchWord, workingDir],
      { maxBuffer: RIPGREP_MAX_BUFFER }
    );

    const stdout = filesContainingWord.stdout.trim();
    return stdout.length > 0 ? stdout.split("\n") : [];
  } catch (error) {
    // Grep and RipGrep retrun exit code 1 when no files are found -> ignore this case
    if (
      error.code === 1 &&
      error.killed === false &&
      error.signal === null &&
      error.stderr === "" &&
      error.stdout === ""
    ) {
      return [];
    } else {
      logger.error("Something went wrong while searching for files", error);
      process.exit(DEFAULT_ERROR_EXIT_CODE);
    }
  }
};

/**
 * Append files containing placeholder value to envVarObjects
 * @param {string} workingDir path to folder where to search for files
 * @returns Promise<Array<Object<{variableName: string, placeholderValue: string, runtimeValue: string, files: <Array[string]}>>>>
 */
const addFileOcurencesForPlaceholders = (workingDir) => {
  const addFileOcurencesForPlaceholdersAsync = async (envVarObjects) => {
    const fullPath = path.resolve(workingDir);
    return await Promise.all(
      envVarObjects.map(async (envVar) => {
        return {
          ...envVar,
          files: await getFilesCotainingString(
            fullPath,
            envVar.placeholderValue
          ),
        };
      })
    );
  };
  return addFileOcurencesForPlaceholdersAsync;
};

const logEnvVarObjects = (envVarObjects) => {
  logger.info("Env var objects:\n", JSON.stringify(envVarObjects, null, 2));
  return envVarObjects;
};

/**
 * Async function to replace string in file
 * @param {string} filepath PAth to file that will get overwritten
 * @param {string} searchString string to be replaced
 * @param {string} replaceString string to replace with
 * @returns Promise<writtenFile>
 */
const replaceStringInFile = async (filepath, searchString, replaceString) => {
  try {
    const fileContent = await promises.readFile(filepath, "utf8");
    const newFileContent = fileContent.replaceAll(searchString, replaceString);
    return promises.writeFile(filepath, newFileContent);
  } catch (error) {
    logger.error(
      `Something went wrong while replacing ${searchString} with ${replaceString} in ${filepath}`,
      error
    );
    process.exit(DEFAULT_ERROR_EXIT_CODE);
  }
};

/**
 *  Replace placeholder values with runtime values in files
 * @param {Array<Object<{variableName: string, placeholderValue: string, runtimeValue: string, files: <Array[string]}>>} envVarObjects
 * @returns {Promise<envVarObjects>}
 */
const replaceEnvVarValuesInFiles = async (envVarObjects) => {
  // We cannot write in paralel the same file, so do not use map/forEach here.
  for (let i = 0; i < envVarObjects.length; i++) {
    const envVar = envVarObjects[i];
    try {
      const writtingFilePromises = envVar.files.map((filepath) =>
        replaceStringInFile(
          filepath,
          envVar.placeholderValue,
          envVar.runtimeValue
        )
      );
      await Promise.all(writtingFilePromises);
    } catch (error) {
      logger.error(
        `Something went wrong while replacing ${envVar.placeholderValue} with ${envVar.runtimeValue} in ${envVar.files}`,
        error
      );
      process.exit(DEFAULT_ERROR_EXIT_CODE);
    }
  }

  return envVarObjects;
};

export const runReplaceScript = async (workingDir, envFilePath) => {
  checkWorkingDirExistsOrAbort(workingDir);
  checkEnvFileExistsOrAbort(envFilePath);

  try {
    await pipeAsync(
      readDotFile,
      generateEnvVarsObjectFromDotFile,
      addRuntimeEnvValues,
      addFileOcurencesForPlaceholders(workingDir),
      logEnvVarObjects,
      replaceEnvVarValuesInFiles
    )(envFilePath);
  } catch (error) {
    logger.error("Error occured while replacing env variables", error);
  }
};
