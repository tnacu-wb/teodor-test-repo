import {
  updateJSON,
  updateLernaConfig,
  updateWorkspaces,
  createDirectory,
  ROOT_DIR,
} from "./utilities/index.mjs";
import { copyRecursiveSync } from "./utils.mjs";
import fs, { copyFileSync } from "fs";
import { run as jscodeshift } from "jscodeshift/src/Runner.js";
import path from "path";
import readline from "readline";
import { fileURLToPath } from "url";

const NO_HOIST_LIBS = ["webpack", "webpack-cli", "ts-loader"];

const THIRD_PARTY_DEPS = {
  next: "12.0.10",
  "next-i18next": "^10.2.0",
  graphql: "^16.11.0",
  "graphql-request": "^6.1.0",
  "i18next-http-backend": "^1.4.0",
  "date-fns": "^4.1.0",
  "react-hook-form": "^7.49.2",
};

const createNPMPackage = async (
  { destPath, srcPath, includeRootDir = true },
  { name = "", deps = {}, scripts = {}, version = "0.0.1", description = "" },
) => {
  const packageJson = `${destPath}/package.json`;
  const srcDir = path.join(destPath, "src");
  // Create the destination directory and the "src" dir in which the sources will go
  await createDirectory(destPath);
  await createDirectory(srcDir);
  // Copy all of the files and folders

  copyResources(srcPath, srcDir, includeRootDir);
  const packageJsonContent = JSON.parse(
    fs.readFileSync(
      path.join(".", "scripts", "config-templates", "package.json"),
      "utf8",
    ),
  );
  packageJsonContent.name = name;
  packageJsonContent.description =
    description || packageJsonContent.description;
  packageJsonContent.dependencies = {
    ...packageJsonContent.dependencies,
    ...deps,
  };
  packageJsonContent.scripts = {
    ...packageJsonContent.scripts,
    ...scripts,
  };
  packageJsonContent.peerDependencies = {
    ...packageJsonContent.peerDependencies,
    ...THIRD_PARTY_DEPS,
  };
  packageJsonContent.devDependencies = {
    ...packageJsonContent.devDependencies,
    ...THIRD_PARTY_DEPS,
  };
  packageJsonContent.version = version;
  // Add the dev libraries to the root package.json if they dont exists yet
  updateJSON(path.join(".", "package.json"), (rootPackageJSON) => {
    Object.keys(packageJsonContent.devDependencies).forEach((lib) => {
      const isSameVersionLib =
        rootPackageJSON.devDependencies[lib] ===
        packageJsonContent.devDependencies[lib];
      const canBeHoisted =
        !rootPackageJSON.devDependencies[lib] || isSameVersionLib;

      const isExcludedFromHoisting = NO_HOIST_LIBS.includes(lib);
      if (canBeHoisted && !isExcludedFromHoisting) {
        rootPackageJSON.devDependencies[lib] =
          packageJsonContent.devDependencies[lib];
        delete packageJsonContent.devDependencies[lib];
      }
    });

    return rootPackageJSON;
  });

  fs.writeFileSync(packageJson, JSON.stringify(packageJsonContent, null, 2));
  fs.copyFileSync(
    path.join(".", "scripts", "config-templates", "webpack.config.js"),
    `${destPath}/webpack.config.js`,
  );
  fs.copyFileSync(
    path.join(".", "scripts", "config-templates", "tsconfig.json"),
    `${destPath}/tsconfig.json`,
  );
  fs.copyFileSync(
    path.join(".", "scripts", "config-templates", "babel.config.js"),
    `${destPath}/babel.config.js`,
  );
};

const relocateCommonUI = async (commonUIPath, destPath) => {
  const jsonContent = JSON.parse(
    fs.readFileSync(path.join(commonUIPath, "package.json"), "utf8"),
  );

  copyRecursiveSync(
    path.join(commonUIPath, ".storybook"),
    path.join(destPath, ".storybook"),
  );
  copyRecursiveSync(
    path.join(commonUIPath, "assets"),
    path.join(destPath, "assets"),
  );
  const componentsDir = [
    path.join(commonUIPath, "src", "components"),
    path.join(commonUIPath, "src", "theme"),
    path.join(commonUIPath, "src", "utils"),
  ];

  await createNPMPackage(
    { destPath: path.join(destPath, "atoms"), srcPath: componentsDir },
    {
      name: `@whitbread-eos/atoms`,
      scripts: {
        ...jsonContent.scripts,
        ...{
          "svg2react-component":
            "../../node_modules/.bin/svgr --replace-attr-values '#58595B={props.color || \"#58595B\"}','#007FAB={props.color || \"#007FAB\"}','#D73D00={props.color || \"#D73D00\"}','#333={props.color || \"#333\"}','#D90941={props.color || \"#D90941\"}','#FC0F42={props.color || \"#FC0F42\"}','#1C8754={props.color || \"#1C8754\"}','#000={props.color || \"#000\"}'  --typescript --out-dir src/assets/icons -- ../assets",
        },
      },
    },
  );
  fs.copyFileSync(
    path.join(commonUIPath, "src", "index.ts"),
    path.join(destPath, "atoms", "src", "index.ts"),
  );
};

const relocatePremierInn = async (premierInnPath, destPath) => {
  const layoutsDir = path.join(premierInnPath, "src", "components", "layouts");
  const hooksDir = path.join(premierInnPath, "src", "hooks");
  const pagesDir = path.join(premierInnPath, "src", "pages");
  const componentsDir = path.join(premierInnPath, "src", "components");
  const utilsDir = path.join(premierInnPath, "src", "utils");
  // Move the hooks
  copyRecursiveSync(hooksDir, `${destPath}/utils/hooks/src/`);
  // Move formatters and validators to their proper place
  await createDirectory(`${destPath}/utils/formatters/src/`);
  await createDirectory(`${destPath}/utils/validators/src/`);
  copyRecursiveSync(
    path.join(utilsDir, "formatters.tsx"),
    `${destPath}/utils/formatters/src/index.tsx`,
  );
  copyRecursiveSync(
    path.join(utilsDir, "formatters.test.tsx"),
    `${destPath}/utils/formatters/src/formatters.test.tsx`,
  );
  copyRecursiveSync(
    path.join(utilsDir, "validators.tsx"),
    `${destPath}/utils/validators/src/index.tsx`,
  );
  copyRecursiveSync(
    path.join(utilsDir, "validators.test.tsx"),
    `${destPath}/utils/validators/src/validators.test.tsx`,
  );
  // Move layouts to their proper place
  await createNPMPackage(
    { destPath: path.join(destPath, "templates"), srcPath: layoutsDir },
    {
      name: `@whitbread-eos/templates`,
    },
  );
  // Move the pages to their proper place
  await relocatePremierInnPages(destPath, pagesDir);
  await relocatePremierInnComponents(destPath, componentsDir);
};

const adjustImportsAndDependencies = async (destPath) => {
  const currentFile = fileURLToPath(import.meta.url);
  const transformPath = path.join(
    path.dirname(currentFile),
    "transforms",
    "updateImportStatements.js",
  );

  const paths = [
    "./apps/next-apps/premier-inn/src/components/hotel-details/Facilities/HotelFacilities/HotelFacilitiesList/HotelFacilitiesList.component.tsx",
  ];
  const options = {
    dry: true,
    print: true,
    verbose: 1,
    importsMap: {
      "~hooks/use-screensize": "@whitbread-eos/utils",
      "~utils/formatters": "@whitbread-eos/utils",
    },
  };

  const res = await jscodeshift(transformPath, paths, options);
};

const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout,
});

rl.on("close", () => {
  console.log("\n");
  process.exit(0);
});

(async function () {
  const relocationDir = ROOT_DIR;
  const premierInnPath = "./apps/next-apps/premier-inn";
  const commonUIPath = "./common/common-ui";
  createAtomicDesignFolders(relocationDir);
  await updateWorkspaces();
  await updateLernaConfig();
  await relocateCommonUI(commonUIPath, relocationDir);
  await relocatePremierInn(premierInnPath, relocationDir);
  await adjustImportsAndDependencies(premierInnPath);
})();

function copyResources(srcPath, srcDir, includeRootDir = true) {
  if (!srcPath) {
    return;
  }

  if (Array.isArray(srcPath)) {
    return srcPath.map((f) => copyResources(f, srcDir));
  }

  if (fs.statSync(srcPath).isDirectory()) {
    const rootDir = srcPath.split(path.sep).pop();
    const destinationDir = includeRootDir ? path.join(srcDir, rootDir) : srcDir;
    copyRecursiveSync(srcPath, destinationDir);
  } else {
    copyFileSync(srcPath, srcDir);
  }
}

function createAtomicDesignFolders(relocationDir) {
  const folders = [
    "atoms",
    "molecules",
    "organisms",
    "templates",
    "pages",
    "utils",
  ];
  folders.forEach((folder) => {
    createDirectory(path.join(relocationDir, folder));
  });
}

async function relocatePremierInnPages(destPath, pagesDir) {
  const pages = {
    "ancillaries.tsx": "ancillaries",
    "confirmation.tsx": "confirmation",
    "payment.tsx": "payment",
    "guest-details.tsx": "guest-details",
    "hotels/[...slug].tsx": "hotel-details",
  };

  Object.keys(pages).forEach(async (fileName) => {
    const destinationPackage = pages[fileName];
    const destinationRoot = path.join(
      destPath,
      "pages",
      destinationPackage.toLowerCase(),
    );

    await createNPMPackage(
      { destPath: destinationRoot, srcPath: null },
      {
        name: `@whitbread-eos/${destinationPackage.toLowerCase()}`,
      },
    );

    copyFileSync(
      path.join(pagesDir, fileName),
      path.join(destinationRoot, "src", "index.tsx"),
    );
    fs.writeFileSync(
      path.join(destinationRoot, "src", "main.ts"),
      `import Page from './index';
export { getServerSideProps } from './index';
export { Page };`,
      { encoding: "utf-8" },
    );
  });
}

async function relocatePremierInnComponents(destPath, componentsDir) {
  const moleculesToProcess = [
    path.join(componentsDir, "hotel-details"),
    path.join(componentsDir, "ancillaries"),
    path.join(componentsDir, "account"),
    path.join(componentsDir, "booking"),
    path.join(componentsDir, "search"),
  ];
  const organismsToProcess = [path.join(componentsDir, "common")];

  await createNPMPackage(
    { destPath: path.join(destPath, "molecules"), srcPath: moleculesToProcess },
    {
      name: `@whitbread-eos/molecules`,
    },
  );
  fs.copyFileSync(
    path.join(componentsDir, "index.ts"),
    path.join(destPath, "molecules", "src", "index.ts"),
  );

  await createNPMPackage(
    { destPath: path.join(destPath, "organisms"), srcPath: organismsToProcess },
    {
      name: `@whitbread-eos/organisms`,
    },
  );
}
