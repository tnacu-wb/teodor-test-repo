/**
 * Card details used for payment in test flows.
 */
export interface CardDetails {
  /** Card number (with or without spaces). */
  number: string;
  /** Cardholder name as printed on the card. */
  name: string;
  /** Expiry month (MM). */
  expiryMonth: string;
  /** Expiry year (YYYY). */
  expiryYear: string;
  /** CVV/security code (optional — PIBA cards do not require CVV). */
  code?: string;
  /** Card type identifier for display/logging. */
  type: string;
  /** Card scheme identifier (e.g. 'PI' for PIBA). Optional. */
  cardSchemeId?: string;
  /** Payment method code used in CCUI validations. */
  paymentMethodCodeCcui?: string;
  /** Payment method code used in PI/Opera validations. */
  paymentMethodCodePi?: string;
  /** Optional descriptive label from legacy datasets. */
  description?: string;
  /** Last 4 digits (legacy validation helpers). */
  last4Digits?: string;
  /** Tokenized card number from legacy datasets. */
  token?: string;
}

// ─── Complete Card Set ─────────────────────────────────────────────────────────

/**
 * Complete card dataset used by PI/IB/CCUI E2E flows.
 */
export class Cards {
  private constructor() {}

  private static futureYear(offset: number): string {
    return String(new Date().getFullYear() + offset);
  }


  static readonly VISA_CREDIT: CardDetails = { name: 'Visa Credit', number: '4444 3333 2222 1111', code: '456', expiryMonth: '02', expiryYear: '2050', type: 'VISA CREDIT', cardSchemeId: 'VS', token: '4764776852337921111' };
  static readonly VISA_DEBIT: CardDetails = { name: 'Visa Debit', number: '4582 6200 0000 0037', code: '456', expiryMonth: '10', expiryYear: '2050', type: 'VISA DEBIT', cardSchemeId: 'DL', token: '4684387551182580037' };
  static readonly ELECTRON: CardDetails = { name: 'Electron', number: '4937 3700 0000 0015', code: '222', expiryMonth: '01', expiryYear: '2050', type: 'ELECTRON', token: '4519938981362400015' };
  static readonly VISA_CARD: CardDetails = { name: 'Test Automation', number: '4111 1111 1111 1103', code: '999', expiryMonth: '01', expiryYear: '2050', type: 'VISA', paymentMethodCodeCcui: 'CVA', paymentMethodCodePi: 'VA', cardSchemeId: 'VS', last4Digits: '1103', token: '4216333880397891103' };
  static readonly MASTERCARD: CardDetails = { name: 'Test Automation', number: '5111 1111 1111 1100', code: '001', expiryMonth: '02', expiryYear: '2050', type: 'MASTERCARD', paymentMethodCodeCcui: 'CMC', paymentMethodCodePi: 'DMC', cardSchemeId: 'MC', last4Digits: '1100', token: '5479321898918651100' };
  static readonly AMEX: CardDetails = { name: 'Test Automation', number: '3755 2666 6666 665', code: '1004', expiryMonth: '01', expiryYear: '2050', type: 'AMEX', paymentMethodCodeCcui: 'CAX', paymentMethodCodePi: 'DAX', cardSchemeId: 'AX', last4Digits: '6665', token: '3492404973517136665' };
  static readonly PIBA_1: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1002 3600 168', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0168', token: '3638074992386880168' };
  static readonly PIBA_2: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1003 2600 019', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0019', token: '3885552881208210019' };
  static readonly PIBA_3: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1003 2600 035', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0035', token: '3139757934110460035' };
  static readonly PIBA_4: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1003 2600 027', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0027', token: '3238099477690090027' };
  static readonly PIBA_5: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1000 2400 226', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0226', token: '3183756746703780226' };
  static readonly PIBA_6: CardDetails = { name: 'Test PIBA Cardholder', number: '3089 5001 1002 0300 028', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card UK', last4Digits: '0028', token: '3492821727807790028' };
  static readonly PIBA_DE: CardDetails = { name: 'Test PIBA Cardholder', number: '6356 2902 1265 9812 804', expiryMonth: '03', expiryYear: '2050', type: 'PIBA', cardSchemeId: 'PI', paymentMethodCodePi: 'BU', description: 'Business Account Card DE', last4Digits: '2804', token: '6895808531819342804' };
  static readonly PREPRODCARD: CardDetails = { name: 'Test Preprod Cardholder', number: '4242 4242 4242 4242', code: '1005', expiryMonth: '01', expiryYear: '2026', type: 'VISA' };

  static readonly DEFAULT_PIBA: CardDetails = Cards.PIBA_6;
  static readonly PIBA_TEST: CardDetails = Cards.PIBA_6;

  // ─── Backward-Compatible Card Aliases ───────────────────────────────────────

  /**
   * PIBA (Premier Inn Business Account) test card - no CVV required.
   */
  static readonly PIBA_CARD: CardDetails = Cards.DEFAULT_PIBA;

  /**
   * Mastercard test card - with CVV.
   */
  static readonly MASTERCARD_CARD: CardDetails = Cards.MASTERCARD;

  /** Default PIBA card for UAT PI tests. */
  static readonly DEFAULT_PIBA_CARD: CardDetails = Cards.DEFAULT_PIBA;

  /**
   * Create a card with custom overrides.
   *
   * @param base - Base card to extend (defaults to VISA_CARD)
   * @param overrides - Fields to override
   */
  static createCard(overrides?: Partial<CardDetails>, base: CardDetails = Cards.VISA_CARD): CardDetails {
    return { ...base, ...overrides };
  }
}
