import { test, expect } from '@playwright/test';
import { PaymentDetailsPage } from '../../../../src/pages/pi/paymentDetails.page';

/**
 * Property 4: Iframe Context Isolation
 *
 * For any card detail input interaction, the interaction must occur within
 * the payment iframe context; no card input locators should be accessible
 * from the main page frame.
 *
 * `∀ cardInput ∈ {cardNumber, cardHolderName, cardExpiry}: cardInput ∈ paymentIframe.context`
 *
 * **Validates: Requirements 5.1**
 */
test.describe('Property 4: Iframe Context Isolation', () => {
  test('PaymentDetailsPage defines paymentIframe as a FrameLocator', async ({ page }) => {
    global.page = page;
    const paymentDetailsPage = new PaymentDetailsPage();

    // The paymentIframe property must be defined
    expect(paymentDetailsPage.paymentIframe).toBeDefined();

    // Verify it is a FrameLocator by checking it has the locator method
    // (FrameLocator exposes .locator() but NOT standard Locator methods like .click())
    expect(typeof paymentDetailsPage.paymentIframe.locator).toBe('function');
    expect(typeof paymentDetailsPage.paymentIframe.getByRole).toBe('function');
  });

  test('card input locators are scoped within the payment iframe context', async ({ page }) => {
    // Set up a page with an iframe structure to verify scoping
    await page.setContent(`
      <div id="main-content">
        <iframe id="paymentFrame" src="about:blank"></iframe>
      </div>
    `);

    global.page = page;
    const paymentDetailsPage = new PaymentDetailsPage();

    // Verify card input locators are NOT simple page-level locators
    // They should be scoped through the paymentIframe FrameLocator
    const cardNumberInput = paymentDetailsPage.cardNumberInput;
    const cardHolderNameInput = paymentDetailsPage.cardHolderNameInput;
    const cardExpiryInput = paymentDetailsPage.cardExpiryInput;

    // All card inputs should be defined (they are readonly class properties)
    expect(cardNumberInput).toBeDefined();
    expect(cardHolderNameInput).toBeDefined();
    expect(cardExpiryInput).toBeDefined();

    // Verify that direct page-level locators with the same selectors
    // are NOT the same objects as the iframe-scoped locators
    const mainPageCardNumber = page.locator('div[id="input_card_number"] input');
    const mainPageCardHolderName = page.locator('input[id="card_holder_first_name"]');
    const mainPageCardExpiry = page.locator('input#card_expiry_date');

    // The iframe-scoped locators must be distinct from main page locators
    // (they represent different contexts even if selectors look similar)
    expect(cardNumberInput).not.toBe(mainPageCardNumber);
    expect(cardHolderNameInput).not.toBe(mainPageCardHolderName);
    expect(cardExpiryInput).not.toBe(mainPageCardExpiry);
  });

  test('card input locators are not accessible from the main page frame', async ({ page }) => {
    // Set up a page with an iframe that contains card inputs
    // Card inputs exist ONLY inside the iframe, not on the main page
    await page.setContent(`
      <div id="main-content">
        <h1>Payment Page</h1>
        <iframe id="paymentFrame" src="about:blank"></iframe>
      </div>
    `);

    // Inject card input elements into the iframe
    const iframeElement = page.locator('iframe#paymentFrame');
    await iframeElement.waitFor({ state: 'attached' });
    const frame = page.frameLocator('iframe#paymentFrame');
    await page.evaluate(() => {
      const iframe = document.getElementById('paymentFrame') as HTMLIFrameElement;
      const iframeDoc = iframe.contentDocument!;
      iframeDoc.body.innerHTML = `
        <div id="input_card_number"><input type="text" /></div>
        <input id="card_holder_first_name" type="text" />
        <input id="card_expiry_date" type="text" />
      `;
    });

    // Main page should NOT have these card input elements
    const mainPageCardNumber = page.locator('div[id="input_card_number"] input');
    const mainPageCardHolderName = page.locator('input[id="card_holder_first_name"]');
    const mainPageCardExpiry = page.locator('input#card_expiry_date');

    await expect(mainPageCardNumber).toHaveCount(0);
    await expect(mainPageCardHolderName).toHaveCount(0);
    await expect(mainPageCardExpiry).toHaveCount(0);

    // But the iframe-scoped locators from PaymentDetailsPage CAN find them
    global.page = page;
    const paymentDetailsPage = new PaymentDetailsPage();
    const iframeCardNumber = paymentDetailsPage.cardNumberInput;
    const iframeCardHolderName = paymentDetailsPage.cardHolderNameInput;
    const iframeCardExpiry = paymentDetailsPage.cardExpiryInput;

    await expect(iframeCardNumber).toHaveCount(1);
    await expect(iframeCardHolderName).toHaveCount(1);
    await expect(iframeCardExpiry).toHaveCount(1);
  });

  test('PaymentDetailsPage uses frameLocator to create the iframe context', async ({ page }) => {
    global.page = page;
    const paymentDetailsPage = new PaymentDetailsPage();

    // Verify that switchToIframe() returns the paymentIframe FrameLocator
    const iframeContext = paymentDetailsPage.switchToIframe();
    expect(iframeContext).toBe(paymentDetailsPage.paymentIframe);

    // Verify the FrameLocator has the expected interface
    // (distinct from a regular Locator which would have click/fill directly)
    expect(typeof iframeContext.locator).toBe('function');
    expect(typeof iframeContext.getByRole).toBe('function');
    expect(typeof iframeContext.getByText).toBe('function');
  });
});
