import { test, expect } from '@playwright/test';
import { Cards, type CardDetails } from '@test-data/cards';
import { PaymentDetailsPage } from '../../../../src/pages/pi/paymentDetails.page';

/**
 * Property 6: PIBA Card No-CVV Invariant
 *
 * For any PIBA card payment submission, no CVV/security code field should be
 * filled or required.
 *
 * `∀ card ∈ PIBACards: card.code === undefined ∧ noCvvFieldRequired(card)`
 *
 * **Validates: Requirements 5.3**
 */
test.describe('Property 6: PIBA Card No-CVV Invariant', () => {
  test('Cards.DEFAULT_PIBA_CARD has code as undefined (CVV not set)', () => {
    // Assert that the PIBA card constant does not define a CVV code
    const card: CardDetails = Cards.DEFAULT_PIBA_CARD;
    expect(card.code).toBeUndefined();
  });

  test('Cards.PIBA_TEST has code as undefined (CVV not set)', () => {
    const card: CardDetails = Cards.PIBA_TEST;
    expect(card.code).toBeUndefined();
  });

  test('PIBA card type is "PIBA"', () => {
    expect(Cards.DEFAULT_PIBA_CARD.type).toBe('PIBA');
  });

  test('PIBA card cardSchemeId is "PI"', () => {
    expect(Cards.DEFAULT_PIBA_CARD.cardSchemeId).toBe('PI');
  });

  test('CardDetails interface makes code optional (PIBA card satisfies interface without code)', () => {
    // Verify a card without `code` is a valid CardDetails — this demonstrates
    // the interface allows omitting the CVV field
    const pibaCard: CardDetails = {
      name: 'Test Cardholder',
      number: '4111 1111 1111 1111 111',
      expiryMonth: '12',
      expiryYear: '2030',
      type: 'PIBA',
      cardSchemeId: 'PI',
      // code intentionally omitted — must compile without it
    };

    expect(pibaCard.code).toBeUndefined();
    expect(pibaCard.type).toBe('PIBA');
  });

  test('PaymentDetailsPage does NOT define any CVV/security code locator', () => {
    // Structurally verify that the PaymentDetailsPage class has no CVV-related
    // locator fields, confirming the no-CVV invariant for PIBA cards
    const proto = PaymentDetailsPage.prototype;
    const classKeys = Object.getOwnPropertyNames(proto);

    // Check that no property or method name references CVV, CVC, or security code
    const cvvRelatedKeys = classKeys.filter(
      (key) =>
        key.toLowerCase().includes('cvv') ||
        key.toLowerCase().includes('cvc') ||
        key.toLowerCase().includes('securitycode') ||
        key.toLowerCase().includes('security_code')
    );

    expect(cvvRelatedKeys).toEqual([]);
  });

  test('PaymentDetailsPage.fillCardDetails does not interact with any CVV field', async ({
    page,
  }) => {
    // Arrange: Set up a mock payment page with the iframe structure including
    // a CVV field that should NOT be interacted with
    await page.setContent(`
      <iframe id="paymentFrame" src="about:blank"></iframe>
    `);

    // Inject content into the iframe including a CVV input
    const frame = page.frameLocator('iframe#paymentFrame');
    await page.evaluate(() => {
      const iframe = document.getElementById('paymentFrame') as HTMLIFrameElement;
      const doc = iframe.contentDocument!;
      doc.open();
      doc.write(`
        <div id="input_card_number"><input type="text" /></div>
        <input id="card_holder_first_name" type="text" />
        <input id="card_expiry_date" type="text" />
        <input id="card_security_code" type="text" data-cvv="true" />
        <input id="paybutton" type="submit" value="Confirm Booking" />
      `);
      doc.close();
    });

    global.page = page;
    const paymentDetailsPage = new PaymentDetailsPage();

    const pibaCard: CardDetails = {
      name: 'Test PIBA Cardholder',
      number: '4111 1111 1111 1111 111',
      expiryMonth: '12',
      expiryYear: '2030',
      type: 'PIBA',
      cardSchemeId: 'PI',
    };

    // Act: Fill card details
    await paymentDetailsPage.fillCardDetails(pibaCard);

    // Assert: CVV field should remain empty — fillCardDetails must NOT touch it
    const cvvValue = await frame.locator('input#card_security_code').inputValue();
    expect(cvvValue).toBe('');
  });
});
