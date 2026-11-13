import { FullConfig } from '@playwright/test';

const sensitiveConfigKey = /password|secret|token|credential|authorization|(?:api|access|private)[_-]?key|username/i;

/**
 * Playwright global setup.
 * This runs once before all tests.
 * You can use it to set up test data, authenticate, etc.
 */
export default async (config: FullConfig): Promise<void> => {
    console.log('Global setup: preparing environment...');
    console.log('Running tests with this config:', JSON.stringify(
        config,
        (key, value) => sensitiveConfigKey.test(key) ? '[REDACTED]' : value,
    ));
    // Example: Set up environment, seed database, or perform login and save storage state
    // await someSetupFunction();
};
