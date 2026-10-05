import { styleText } from "node:util";

let VERBOSE_LEVEL = 1;

// `styleText` is a Node built-in (stable since Node 22 LTS): it honours NO_COLOR /
// FORCE_COLOR and strips escape codes when the target stream is not a TTY, so container
// logs stay clean without pulling in a colour dependency.
const style = (format, text, stream) => styleText(format, text, { stream });

export const logger = {
  init: (verboseLevel) => {
    VERBOSE_LEVEL = verboseLevel;
  },
  debug: (...args) => {
    if (VERBOSE_LEVEL >= 2) {
      console.log(style("dim", "[debug]", process.stdout), ...args);
    }
  },
  info: (...args) => {
    if (VERBOSE_LEVEL >= 1) {
      console.log(style("cyan", "[info]", process.stdout), ...args);
    }
  },
  warn: (...args) => {
    console.log(style("yellow", "[warn]", process.stdout), ...args);
  },
  error: (...args) => {
    console.error(style(["red", "bold"], "[error]", process.stderr), ...args);
  },
};
