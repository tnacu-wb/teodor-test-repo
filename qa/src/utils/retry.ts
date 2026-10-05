/**
 * Retry utilities for flaky operations (e.g. button clicks that don't register,
 * elements that re-render mid-interaction).
 */

export interface RetryOptions {
  /** Maximum number of attempts (default: 3). */
  maxAttempts?: number;
  /** Delay between attempts in ms (default: 1000). */
  delayMs?: number;
  /** Optional description for error messages. */
  description?: string;
}

/**
 * Retry an async operation until it succeeds or exhausts attempts.
 *
 * @param fn - The async function to retry
 * @param options - Retry configuration
 * @returns The result of the successful attempt
 * @throws The last error if all attempts fail
 */
export async function retry<T>(fn: () => Promise<T>, options: RetryOptions = {}): Promise<T> {
  const { maxAttempts = 3, delayMs = 1000, description = 'operation' } = options;
  let lastError: Error | undefined;

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    try {
      return await fn();
    } catch (error) {
      lastError = error instanceof Error ? error : new Error(String(error));
      if (attempt < maxAttempts) {
        await new Promise((resolve) => setTimeout(resolve, delayMs));
      }
    }
  }

  throw new Error(
    `${description} failed after ${maxAttempts} attempts. Last error: ${lastError?.message}`
  );
}

/**
 * Wait for a condition to become true, polling at intervals.
 *
 * @param condition - Function that returns true when condition is met
 * @param options - Retry configuration
 * @throws Error if condition is not met within the attempts
 */
export async function waitForCondition(
  condition: () => Promise<boolean>,
  options: RetryOptions = {}
): Promise<void> {
  const { maxAttempts = 10, delayMs = 500, description = 'condition' } = options;

  for (let attempt = 1; attempt <= maxAttempts; attempt++) {
    if (await condition()) {
      return;
    }
    if (attempt < maxAttempts) {
      await new Promise((resolve) => setTimeout(resolve, delayMs));
    }
  }

  throw new Error(`${description} was not met after ${maxAttempts} attempts (${maxAttempts * (options.delayMs ?? 500)}ms total)`);
}
