import { test, expect } from '@fixtures/pi.fixture';
import { Hotels } from '@test-data/hotels';

test.describe('PI Homepage - Search Console', () => {
  test.beforeEach(async ({ pages }) => {
    await pages.homePage.open();
  });

  test('should load the PI homepage successfully', async ({ page }) => {
    await expect(page).toHaveTitle(/Premier Inn/i);
  });

  test('should search for a hotel by name', async ({ pages }) => {
    await pages.homePage.searchForHotel(Hotels.DEFAULT_HOTEL.name);
    // Verify a location was selected (the autocomplete fills in the full address)
    await expect(pages.homePage.searchLocationInput).not.toHaveValue('');
  });
});
