import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('CCUI Smoke Pack', () => {
  test('CCUI homepage loads @smoke @TC-809', async ({ page }) => {
    await page.goto('https://ccui.uat.premierinn.digital/gb/en');
    const title = await page.title();
    expect(title).toBeTruthy();
  });

  test('CCUI page is accessible @smoke @TC-810', async ({ page }) => {
    await page.goto('https://ccui.uat.premierinn.digital/gb/en');
    await expect(page).not.toHaveTitle('');
  });
});
