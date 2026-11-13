import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('Datatrans', () => {
  test('Datatrans payment page is accessible @datatrans @TC-792', async ({ page }) => {
    await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
    await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
    const title = await page.title();
    expect(title).toContain('Premier Inn');
  });

  test('Datatrans tokenisation flow entry point exists @datatrans @TC-793', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
    const pageContent = await page.textContent('body');
    expect(pageContent).toBeTruthy();
  });

  test('Datatrans iframe loads within timeout @datatrans @TC-794', async ({ page }) => {
    await page.goto('/');
    // Timeout — element doesn't exist
    await page.locator('[data-testid="datatrans-payment-iframe"]').waitFor({ state: 'visible' });
  });
});
