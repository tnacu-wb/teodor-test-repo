import { test, expect } from '../../../config/lambdatest/fixture';

test.describe('PI Regression Pack', () => {
  test('Manage Booking modal opens from header @regression @TC-811', async ({ page }) => {
    await page.goto('/');
    await page.locator('[data-testid="ManageBookingButton"]').click();
    await expect(page.locator('[data-testid="ManageBookingModal-ModalContent"]')).toBeVisible();
  });

  test('Hotel search returns availability results @regression @TC-812', async ({ page }) => {
    await page.goto('/gb/en/hotels/england/west-sussex/crawley/london-gatwick-airport-south-london-road.html');
    await expect(page.locator('h1[data-testid="hdp_hotelTitle"]')).toBeVisible({ timeout: 30000 });
    await expect(page.locator('[data-testid="hdp_basketStayPrice"]')).toBeVisible();
  });
});
