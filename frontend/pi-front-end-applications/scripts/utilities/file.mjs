import fs, { copyFileSync } from "fs";
import path from "path";

export const ROOT_DIR = "./pi-components-catalog";
export function updateJSON(file, callback) {
  const jsonContent = JSON.parse(fs.readFileSync(file, "utf8"));
  const stringifiedContent = JSON.stringify(callback(jsonContent), null, 2)
  fs.writeFileSync(file, stringifiedContent);
}

export function updateLernaConfig() {
  updateJSON(path.join(".", "lerna.json"), (json) => {
    if (!json.packages.includes(`${ROOT_DIR}/*`)) {
      json.packages.push(`${ROOT_DIR}/*`);
    }
    if (!json.packages.includes(`${ROOT_DIR}/pages/*`)) {
      json.packages.push(`${ROOT_DIR}/pages/*`);
    }
    if (!("useWorkspaces" in json)) {
      json.useWorkspaces = true;
    }

    return json;
  });
}

export function updateWorkspaces() {
  updateJSON(path.join(".", "package.json"), (json) => {
    json.workspaces = [
      `${ROOT_DIR}/atoms`,
      `${ROOT_DIR}/molecules`,
      `${ROOT_DIR}/organisms`,
      `${ROOT_DIR}/pages/*`,
      `${ROOT_DIR}/templates`,
      `${ROOT_DIR}/utils`,
    ];
    json.nohoist = ["**/webpack", "**/webpack-cli", '**/ts-loader', '**/ts-loader/**'];

    return json;
  });
}

export const createDirectory = async (directoryPath) => {
  if (!fs.existsSync(directoryPath)) {
    fs.mkdirSync(directoryPath, { recursive: true });
  }
};
