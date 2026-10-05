import readline from "readline";
import chalk from "chalk";
import path from "path";

import {
  copyRecursiveSync,
  replaceTextInFile,
  premierInnLogo,
  getPath,
  question,
  validatePackageName,
  validatePath,
} from "./utils.mjs";

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

rl.on("close", () => {
  console.log("\n");
  process.exit(0);
});

console.log(premierInnLogo() + "Premier Inn Next JS App Deployment \n");

const askQuestions = async () => {
  const applicationsPath = getPath("apps");
  const templatePath = getPath("template/nextjs");
  let packageName = null;
  let newAppPath = null;

  while (!validatePackageName(packageName)) {
    packageName = await question(rl, "Enter the new package name :");

    if (!validatePackageName(packageName)) {
      console.log(
        chalk.red("Invalid package name, please enter a valid package name")
      );
    }
  }

  while (!validatePath(applicationsPath, newAppPath)) {
    newAppPath = await question(
      rl,
      "Please enter path within the apps folder\n(e.g. premier-inn/hotel-details) this is where the application will be deployed to :"
    );
    if (!validatePath(applicationsPath, newAppPath)) {
      console.log(chalk.red("Path exists, please enter a valid path"));
    }
  }

  const newAppDestPath = path.resolve(applicationsPath, newAppPath);

  // Copy the template to the new apps folder
  copyRecursiveSync(templatePath, newAppDestPath);

  await replaceTextInFile(
    path.resolve(newAppDestPath, 'package.json'),
    '{packageName}',
    packageName
  );

  console.log("\nApp Deployed.... !!!\n");

  rl.close();
};

(async () => askQuestions())();
