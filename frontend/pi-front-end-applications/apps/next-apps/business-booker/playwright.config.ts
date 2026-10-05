import { config } from '@WB-playwright/config';
import { defineConfig, devices } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config({
  path: ['.env.local', '.env'],
});

const deviceConfig = config.DEVICE === 'desktop' ? devices['Desktop Chrome'] : devices['iPhone 11'];
const viewport =
  config.DEVICE === 'desktop' ? { width: 1920, height: 1080 } : deviceConfig.viewport;

export default defineConfig({
  testDir: './playwright/tests',
  fullyParallel: false,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: 'html',
  timeout: 120000,
  expect: {
    timeout: 10000,
  },
  use: {
    trace: 'on-first-retry',
  },
  projects: [
    {
      name: `Automation Tests`,
      use: {
        browserName: config.DEVICE === 'desktop' ? 'chromium' : 'webkit',
        ...deviceConfig,
        viewport: viewport,
      },
    },
  ],
});
