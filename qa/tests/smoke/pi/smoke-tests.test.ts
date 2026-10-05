import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('PI Smoke Pack', () => {
  test('Homepage loads successfully @smoke @TC-805', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Premier Inn/, { timeout: 30000 });
  });

  test('Hotel Details Page loads successfully @smoke @TC-806', async ({ page }) => {
    await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
    await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
  });
});
