import { logger } from "./logger.mjs";
import { performance } from "perf_hooks";

export const pipeAsync =
  (...fnList) =>
  async (x) => {
    let passableArgument = x;
    for (let i = 0; i < fnList.length; i++) {
      const nextFunction = fnList[i];
      const functionName = nextFunction.name || "anonymous function";

      const startTime = performance.now();
      passableArgument = await nextFunction(passableArgument);
      const endTime = performance.now();

      const diffTimeInSeconds = ((endTime - startTime) / 1000).toFixed(4);
      logger.info(
        `[PERFORMANCE] Function ${functionName} took ${diffTimeInSeconds} s`
      );
    }

    return passableArgument;
  };
