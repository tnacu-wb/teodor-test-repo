import fs from "fs";
import path from "path";
import chalk from "chalk";

/**
 * copyFiles Recursively
 * @param src
 * @param dest
 */
export const copyRecursiveSync = (src, dest) => {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();
  if (isDirectory) {
    fs.mkdirSync(dest, { recursive: true });
    fs.readdirSync(src).forEach(function (childItemName) {
      copyRecursiveSync(
        path.join(src, childItemName),
        path.join(dest, childItemName)
      );
    });
  } else {
    fs.copyFileSync(src, dest);
  }
};

/**
 * replaces text within file
 * @param file
 * @param find
 * @param replacement
 */
export const replaceTextInFile = (file, find, replacement) =>
  new Promise((resolve, reject) => {
    if (fs.existsSync(file)) {
      fs.readFile(file, "utf8", function (err, data) {
        if (err) {
          reject(err);
        }
        const result = data.replace(find, replacement);

        fs.writeFile(file, result, "utf8", function (err) {
          if (err) reject(err);
          resolve();
        });
      });
    } else {
      console.log(`File not found ${file}, skipping..`);
      resolve();
    }
  });

/**
 * premier inn logo wrapped in chalk
 * @return {string}
 */
export const premierInnLogo = () =>
  chalk.magenta(
    "\n" +
      "\n" +
      "\n" +
      "\n" +
      "\n" +
      "              @@#\n" +
      "              +@@@#\n" +
      "           ,   @@@@@\n" +
      "          `@;   @@@@@\n" +
      "     :`    `    #@@@@@\n" +
      "   ,@@,          @@@@@#       `'@@@:                                   +@                     +@;\n" +
      "   @@@+'         @@@@@@       +@@@@@@,                                 @@.                    @@+\n" +
      "    @@`          @@@@@@'      +@@  ;@@                                 `;                     @@+\n" +
      "      ;          @@@@@@@      +@@   @@@     .    `.         .`    .           `.         .    @@+     `.        .`\n" +
      "                 @@@@@@@      +@@   .@@ @@ @@@ '@@@@@   @@'@@@@ @@@@@  @@   +@@@@@   @@ @@;   @@+ @@ @@@@,  @@'@@@@\n" +
      "                `@@@@@@@      +@@   .@@ @@@@+,,@@  ;@@  @@@,.@@@@.;@@. @@  :@@  ;@@  @@@@+.   @@+ @@@# @@@  @@@:.@@@\n" +
      "     :`         @@@@@@@@      +@@   @@@ @@@   @@    @@` @@.  ,@@   @@+ @@  @@    @@  @@@      @@+ @@@   @@  @@,   @@\n" +
      "    `@@@      :@@`@@@@@@      +@@  '@@  @@.   @@    ;@+ @@    @@   ;@+ @@  @@    ;@' @@       @@+ @@,   @@  @@    @@\n" +
      "    ;@@      @@@@@+@@@@@      +@@@@@@,  @@    @@@@@@@@@ @@    @@   ;@+ @@  @@@@@@@@+ @@       @@+ @@.   @@  @@    @@\n" +
      "     `       `@@@@@@@@@@      +@@@+,    @@    @@        @@    @@   ;@+ @@  @@        @@       @@+ @@.   @@  @@    @@\n" +
      "             @@@@@@@@@@,      +@@       @@    @@        @@    @@   ;@+ @@  @@        @@       @@+ @@.   @@  @@    @@\n" +
      "           : '@@@@@@@@@       +@@       @@    #@@   #@  @@    @@   ;@+ @@  @@@   @@  @@       @@+ @@.   @@  @@    @@\n" +
      "   '@:,:+@@@@. `@@@@@@.       +@@       @@     @@@@@@@  @@    @@   ;@+ @@   @@@@@@@  @@       @@+ @@.   @@  @@    @@\n" +
      "   ;@@@@@@@@@@@@@@@@@@        :@'       +@      .@@@`   #@    @+   ,@; +@    ,@@@`   +@       '@; +@`   +@  #@    #+\n" +
      "    +@@@@@@@@@@@@@@@@\n" +
      "     :@@@@@@@@@@@@@#\n" +
      "       @@@@@@@@@@@`\n" +
      "         :@@@@@'\n" +
      "\n" +
      "\n" +
      "\n"
  );

/**
 * getPath
 * @param folder
 * @return string
 */
export const getPath = (folder) =>
  (fs.existsSync(path.resolve(".", folder)) && path.resolve(".", folder)) ||
  (fs.existsSync(path.resolve("..", folder)) && path.resolve("..", folder));

/**
 * Question wrapped in promise
 * @param rl
 * @param question
 */
export const question = (rl, question) =>
  new Promise((resolve) =>
    rl.question(chalk.cyan(question), (answer) => {
      resolve(answer);
    })
  );

/**
 * function to validate a npm package name
 * @param packageName
 * @returns {boolean}
 */
export const validatePackageName = (packageName) => {
  const packageNameRegex = new RegExp(
    /^(@[a-z0-9-~][a-z0-9-._~]*\/)?[a-z0-9-~][a-z0-9-._~]*$/
  );
  return packageName && packageNameRegex.test(packageName);
};

export const validatePath = (applicationsPath, destAppPath) => {
  if (destAppPath) {
    const destPath = path.resolve(applicationsPath, destAppPath);
    return !fs.existsSync(destPath);
  }
  return false;
};
