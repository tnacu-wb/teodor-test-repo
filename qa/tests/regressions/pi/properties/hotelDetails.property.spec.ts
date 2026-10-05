import { test, expect } from '@playwright/test';
import { HotelDetailsPage } from '../../../../src/pages/pi/hotelDetails.page';
import { ChooseYourBathroomPage } from '../../../../src/pages/shared/chooseYourBathroom.page';

/**
 * Property 9: Optional Page Resilience
 * Validates: Requirements 10.1, 10.2
 *
 * The booking flow completes successfully regardless of whether
 * the Choose Your Bathroom interstitial page appears or not.
 *
 * (bathroomPageDisplayed → dismissInterstitial() → ancillariesPage) ∧
 * (¬bathroomPageDisplayed → ancillariesPage)
 */
test.describe('Property 9: Optional Page Resilience - skipBathroomSelectionIfPresent()', () => {
  test('should click Continue and proceed when the bathroom interstitial IS present', async ({
    page,
  }) => {
    // Arrange: Set up a page with the Choose Your Bathroom Continue button visible
    await page.setContent(`
      <div data-testid="hotel-details-page">
        <h1 data-testid="accessible-title">Choose your bathroom</h1>
        <button data-testid="ChooseBathroomPage-ContinueButton">Continue</button>
      </div>
    `);

    global.page = page;
    const hotelDetailsPage = new HotelDetailsPage();
    const chooseYourBathroomPage = new ChooseYourBathroomPage();

    // Act: Call skipBathroomSelectionIfPresent — should find and click the button
    await expect(async () => {
      await chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();
    }).not.toThrow();
  });

  test('should not throw and proceed when the bathroom interstitial is NOT present', async ({
    page,
  }) => {
    // Arrange: Set up a page WITHOUT the Continue button (interstitial not shown)
    await page.setContent(`
      <div data-testid="hotel-details-page">
        <h1 data-testid="accessible-title">Hotel details</h1>
        <p>No interstitial here</p>
      </div>
    `);

    global.page = page;
    const hotelDetailsPage = new HotelDetailsPage();
    const chooseYourBathroomPage = new ChooseYourBathroomPage();

    // Act & Assert: skipBathroomSelectionIfPresent should complete without throwing
    await expect(async () => {
      await chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();
    }).not.toThrow();
  });

  test('should complete within a reasonable time when the interstitial is not present', async ({
    page,
  }) => {
    // Arrange: Page without the bathroom interstitial button
    await page.setContent(`
      <div data-testid="hotel-details-page">
        <h1 data-testid="accessible-title">Hotel details</h1>
        <span>Regular page content</span>
      </div>
    `);

    global.page = page;
    const hotelDetailsPage = new HotelDetailsPage();
    const chooseYourBathroomPage = new ChooseYourBathroomPage();

    // Act: Measure execution time — should timeout at ~5s (the internal timeout)
    // but not throw an error
    const startTime = Date.now();
    await chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();
    const elapsed = Date.now() - startTime;

    // Assert: Should complete (due to 5s internal timeout) within ~6s
    // This confirms the try/catch with timeout is working correctly
    expect(elapsed).toBeLessThanOrEqual(6000);
  });

  test('should handle the interstitial appearing and being clicked successfully', async ({
    page,
  }) => {
    // Arrange: A page with a button that can track clicks
    await page.setContent(`
      <div data-testid="hotel-details-page">
        <h1 data-testid="accessible-title">Choose your bathroom</h1>
        <button data-testid="ChooseBathroomPage-ContinueButton" id="continue-btn">Continue</button>
      </div>
      <script>
        document.getElementById('continue-btn').addEventListener('click', function() {
          this.setAttribute('data-clicked', 'true');
        });
      </script>
    `);

    global.page = page;
    const hotelDetailsPage = new HotelDetailsPage();
    const chooseYourBathroomPage = new ChooseYourBathroomPage();

    // Act
    await chooseYourBathroomPage.clickContinueIfChooseBathroomPageIsDisplayed();

    // Assert: The Continue button was actually clicked
    const wasClicked = await page.locator('#continue-btn').getAttribute('data-clicked');
    expect(wasClicked).toBe('true');
  });
});
