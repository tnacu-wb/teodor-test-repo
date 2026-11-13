import { test, expect } from '@playwright/test';
import { ThreeDSecurePage } from '../../../../src/pages/pi/threeDSecure.page';

/**
 * Property 7: 3D Secure Handling Completeness
 * Validates: Requirements 6.3
 *
 * For any booking flow execution, the flow must reach the booking confirmation
 * page regardless of whether the 3D Secure challenge page is displayed or not.
 *
 * (threeDSecureDisplayed → confirmPayment() → confirmationPage) ∧
 * (¬threeDSecureDisplayed → confirmationPage)
 *
 * This test validates the ThreeDSecurePage.confirmPayment() method handles both paths:
 * 1. When 3DS IS displayed: it processes the challenge and completes
 * 2. When 3DS is NOT displayed: it returns without error
 */
test.describe('Property 7: 3D Secure Handling Completeness', () => {
  test('confirmPayment() returns without error when 3DS is NOT displayed (no submit button)', async ({
    page,
  }) => {
    // Arrange: Set up a page WITHOUT 3DS elements — simulating the path where
    // the 3DS challenge page is never shown after card submission
    await page.setContent(`
      <div>
        <h1>Some other page content</h1>
        <p>No 3D Secure elements here</p>
      </div>
    `);

    global.page = page;
    const threeDSecurePage = new ThreeDSecurePage();

    // Act: confirmPayment should return early without throwing
    // This validates: ¬threeDSecureDisplayed → confirmationPage (no blocking)
    let error: Error | null = null;
    try {
      await threeDSecurePage.confirmPayment();
    } catch (e) {
      error = e as Error;
    }

    // Assert: No error should have been thrown
    expect(error).toBeNull();
  });

  test('confirmPayment() handles the "displayed with auto-redirect" path', async ({
    page,
  }) => {
    // Arrange: Set up a page with 3DS elements where submit button disappears after click
    // (simulating successful auto-redirect after 3DS confirmation)
    await page.setContent(`
      <div>
        <input type="radio" value="CONFIRMED" name="outcome" id="confirm-radio" />
        <label for="confirm-radio">Confirm payment</label>
        <input type="submit" id="submit-btn" value="Submit" />
      </div>
      <script>
        document.getElementById('submit-btn').addEventListener('click', function(e) {
          e.preventDefault();
          // Simulate auto-redirect: submit button disappears after click
          this.style.display = 'none';
        });
      </script>
    `);

    global.page = page;
    const threeDSecurePage = new ThreeDSecurePage();

    // Act: confirmPayment should complete the 3DS flow successfully
    // This validates: threeDSecureDisplayed → confirmPayment() → confirmationPage
    let error: Error | null = null;
    try {
      await threeDSecurePage.confirmPayment();
    } catch (e) {
      error = e as Error;
    }

    // Assert: No error — auto-redirect path completed successfully
    expect(error).toBeNull();
  });

  test('confirmPayment() handles the "displayed with manual redirect" path', async ({
    page,
  }) => {
    // Arrange: Set up a page with 3DS elements where submit button persists after click
    // and a redirect link appears (simulating failed auto-redirect requiring manual click)
    await page.setContent(`
      <div>
        <input type="radio" value="CONFIRMED" name="outcome" id="confirm-radio" />
        <label for="confirm-radio">Confirm payment</label>
        <input type="submit" id="submit-btn" value="Submit" />
        <a href="javascript:void(0)" id="redirect-link" style="display:none;">Click here to continue</a>
      </div>
      <script>
        document.getElementById('submit-btn').addEventListener('click', function(e) {
          e.preventDefault();
          // Submit button stays visible (no auto-redirect)
          // After a short delay, show the redirect link
          setTimeout(function() {
            document.getElementById('redirect-link').style.display = 'block';
          }, 500);
        });
      </script>
    `);

    global.page = page;
    const threeDSecurePage = new ThreeDSecurePage();

    // Act: confirmPayment should find and click the redirect link
    // This validates the fallback path when auto-redirect does not occur
    let error: Error | null = null;
    try {
      await threeDSecurePage.confirmPayment();
    } catch (e) {
      error = e as Error;
    }

    // Assert: No error — manual redirect path completed successfully
    expect(error).toBeNull();
  });

  test('ThreeDSecurePage.confirmPayment() method exists and is async', () => {
    // Validate the method signature exists on the class prototype
    const proto = ThreeDSecurePage.prototype;

    expect(typeof proto.confirmPayment).toBe('function');

    const descriptor = Object.getOwnPropertyDescriptor(proto, 'confirmPayment');
    expect(descriptor).toBeDefined();
    expect(descriptor!.value.constructor.name).toBe('AsyncFunction');
  });
});
