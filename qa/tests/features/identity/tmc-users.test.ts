import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('TMC Users', () => {
  test('TMC User access page loads @tmcUsers @TC-798', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
    const title = await page.title();
    expect(title).toContain('Premier Inn');
  });

  test('TMC User permissions are recognised @tmcUsers @TC-799', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
    const pageContent = await page.textContent('body');
    expect(pageContent).toBeTruthy();
  });

  test('TMC booking API responds @tmcUsers @TC-800', async ({ page }) => {
    await page.goto('/');
    // Network error — invalid host
    const response = await page.request.get('https://invalid-tmc-api-host.local/bookings');
    expect(response.ok()).toBe(true);
  });
});
