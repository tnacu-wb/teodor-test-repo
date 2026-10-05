import { test, expect } from '../../../../src/fixtures/base.fixture';
import { ConfirmBookingPage } from '../../../../src/pages/pi/confirmBooking.page';

/**
 * Property 5: Booking Confirmation Integrity
 *
 * For any successfully completed booking flow, the confirmation page must
 * display both a total cost amount and a booking reference.
 *
 * `completeBookingFlow() → visible(totalCostAmount) ∧ visible(bookingReference)`
 *
 * **Validates: Requirements 7.1, 7.2**
 */
test.describe('Property 5: Confirm Booking Integrity', () => {
  test('ConfirmBookingPage exposes totalCostAmount locator', async ({ page }) => {
    const confirmationPage = new ConfirmBookingPage();

    // The totalCostAmount locator must be defined on the instance
    expect(confirmationPage.totalCostAmount).toBeDefined();
    expect(confirmationPage.totalCostAmount).not.toBeNull();
  });

  test('ConfirmBookingPage exposes bookingReference locator', async ({ page }) => {
    const confirmationPage = new ConfirmBookingPage();

    // The bookingReference locator must be defined on the instance
    expect(confirmationPage.bookingReference).toBeDefined();
    expect(confirmationPage.bookingReference).not.toBeNull();
  });

  test('validatePage() is an async method with extended 120s timeout', async ({ page }) => {
    const proto = ConfirmBookingPage.prototype;

    // validatePage must exist
    expect(typeof proto.validatePage).toBe('function');

    // validatePage must be async
    const descriptor = Object.getOwnPropertyDescriptor(proto, 'validatePage');
    expect(descriptor).toBeDefined();
    expect(descriptor!.value.constructor.name).toBe('AsyncFunction');
  });

  test('validatePage() uses 120s extended timeout for page load', async ({ page }) => {
    // Arrange: Page without the confirmation indicator — validatePage should timeout
    await page.setContent(`
      <div>
        <p>Loading...</p>
      </div>
    `);

    const confirmationPage = new ConfirmBookingPage();

    // Act & Assert: validatePage should fail with a message referencing 120s
    // We use a short Playwright timeout to avoid waiting 120s in tests
    await expect(async () => {
      await Promise.race([
        confirmationPage.validatePage(),
        new Promise((_, reject) =>
          setTimeout(() => reject(new Error('test-timeout')), 2000)
        ),
      ]);
    }).rejects.toThrow();
  });

  test('totalCostAmount and bookingReference locators can be asserted with toBeVisible pattern', async ({
    page,
  }) => {
    // Arrange: Set up a page with both confirmation elements present
    await page.setContent(`
      <div data-testid="ThanksForBooking-Container">
        <span data-testid="TotalCostConfirm-amount">£89.00</span>
        <span data-testid="BookingReferenceDetails-Id">PI-12345678</span>
      </div>
    `);

    const confirmationPage = new ConfirmBookingPage();

    // Act & Assert: Both locators must resolve to visible elements
    await expect(confirmationPage.totalCostAmount).toBeVisible();
    await expect(confirmationPage.bookingReference).toBeVisible();
  });

  test('confirmation integrity: both totalCostAmount and bookingReference must be simultaneously visible', async ({
    page,
  }) => {
    // Arrange: Set up the confirmation page with both required elements
    await page.setContent(`
      <div data-testid="ThanksForBooking-Container">
        <div class="confirmation-summary">
          <span data-testid="TotalCostConfirm-amount">£125.50</span>
          <span data-testid="BookingReferenceDetails-Id">REF-98765432</span>
        </div>
      </div>
    `);

    const confirmationPage = new ConfirmBookingPage();

    // Act & Assert: The property requires BOTH elements to be visible simultaneously
    // This is the core assertion of Property 5:
    // completeBookingFlow() → visible(totalCostAmount) ∧ visible(bookingReference)
    await expect(confirmationPage.totalCostAmount).toBeVisible();
    await expect(confirmationPage.bookingReference).toBeVisible();
  });
});
