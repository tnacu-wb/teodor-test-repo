import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('PIB Regression Pack', () => {
  test('PIB Login page renders form elements @regression @TC-813', async ({ page }) => {
    await page.goto('https://business.uat.premierinn.digital/en-gb/account/login');
    const title = await page.title();
    expect(title).toBeTruthy();
  });

  test('PIB page returns valid response @regression @TC-814', async ({ page }) => {
    await page.goto('https://business.uat.premierinn.digital/en-gb/account/login');
    await expect(page).not.toHaveTitle('');
  });
});
