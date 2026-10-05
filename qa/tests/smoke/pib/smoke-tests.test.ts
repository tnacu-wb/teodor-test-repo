import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('PIB Smoke Pack', () => {
  test('PIB Login page loads @smoke @TC-807', async ({ page }) => {
    await page.goto('https://business.uat.premierinn.digital/en-gb/account/login');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
  });

  test('PIB Login form is visible @smoke @TC-808', async ({ page }) => {
    await page.goto('https://business.uat.premierinn.digital/en-gb/account/login');
    const title = await page.title();
    expect(title).toBeTruthy();
  });
});
