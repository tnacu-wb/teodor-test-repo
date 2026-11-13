import { FullConfig } from '@playwright/test';

/**
 * Playwright global teardown.
 * This runs once after all tests.
 * You can use it to clean up test data, close services, etc.
 * @param config - The Playwright configuration
 */
export default async (config: FullConfig): Promise<void> => {
    console.log('Global teardown: cleaning up environment...');
    // Example: Clean up environment, remove test data, close connections
    // await someTeardownFunction();
};