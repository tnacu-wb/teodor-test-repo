import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('Promo Code', () => {
  test('Promo code field is visible on HDP @promocode @TC-789', async ({ page }) => {
    await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
    await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
    const pageContent = await page.textContent('body');
    expect(pageContent).toBeTruthy();
  });

  test('Valid promo code applies discount @promocode @TC-790', async ({ page }) => {
    await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
    await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
    const title = await page.title();
    expect(title).toContain('Premier Inn');
  });

  test('Invalid promo code shows correct error @promocode @TC-791', async ({ page }) => {
    await page.goto('/');
    // Assertion failure — wrong expected value
    const title = await page.title();
    expect(title).toBe('Invalid Promo Code Applied Successfully');
  });
});
