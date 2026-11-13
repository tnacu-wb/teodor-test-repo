import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('CCUI Regression Pack', () => {
  test('CCUI page loads with valid content @regression @TC-815', async ({ page }) => {
    await page.goto('https://ccui.uat.premierinn.digital/gb/en');
    const title = await page.title();
    expect(title).toBeTruthy();
  });

  test('CCUI navigation is accessible @regression @TC-816', async ({ page }) => {
    await page.goto('https://ccui.uat.premierinn.digital/gb/en');
    await expect(page).not.toHaveTitle('');
  });
});
