import { test, expect } from '@playwright/test';
import type { GuestAddress, GuestInfo } from '@test-data/guestData';
import { GuestData } from '@test-data/guestData';

/**
 * Property 2: Guest Details Persistence
 * Validates: Requirements 3.2, 3.3
 *
 * For any valid guest information (GuestInfo and GuestAddress objects),
 * the Guest Details form submission must be accepted without validation errors.
 *
 * ∀ guestData ∈ ValidGuestInfo, address ∈ ValidGuestAddress:
 *   fillGuestDetails(guestData, address) → no validation errors displayed
 *
 * This test validates the test data itself conforms to the expected validation
 * rules, ensuring that when used in the E2E flow, the form will accept the data.
 */

const UK_POSTCODE_REGEX = /^[A-Z]{1,2}\d[A-Z\d]?\s?\d[A-Z]{2}$/i;
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

test.describe('Property 2: Guest Details Persistence', () => {
  /**
   * **Validates: Requirements 3.2**
   * Booker information fields must all be non-empty strings.
   */
  test('GuestData.GUEST_INFO has all required booker information fields as non-empty strings', () => {
    const guest: GuestInfo = GuestData.GUEST_INFO;

    expect(guest.title).toBeTruthy();
    expect(typeof guest.title).toBe('string');
    expect(guest.title.length).toBeGreaterThan(0);

    expect(guest.firstName).toBeTruthy();
    expect(typeof guest.firstName).toBe('string');
    expect(guest.firstName.length).toBeGreaterThan(0);

    expect(guest.lastName).toBeTruthy();
    expect(typeof guest.lastName).toBe('string');
    expect(guest.lastName.length).toBeGreaterThan(0);

    expect(guest.emailAddress).toBeTruthy();
    expect(typeof guest.emailAddress).toBe('string');
    expect(guest.emailAddress.length).toBeGreaterThan(0);

    expect(guest.mobile).toBeTruthy();
    expect(typeof guest.mobile).toBe('string');
    expect(guest.mobile.length).toBeGreaterThan(0);

    expect(guest.landline).toBeTruthy();
    expect(typeof guest.landline).toBe('string');
    expect(guest.landline.length).toBeGreaterThan(0);
  });

  /**
   * **Validates: Requirements 3.2**
   * Email address must match a valid email format (contains @, valid domain).
   */
  test('GuestData.GUEST_INFO email address matches valid email format', () => {
    const guest: GuestInfo = GuestData.GUEST_INFO;

    expect(guest.emailAddress).toMatch(EMAIL_REGEX);
    expect(guest.emailAddress).toContain('@');

    const [localPart, domain] = guest.emailAddress.split('@');
    expect(localPart.length).toBeGreaterThan(0);
    expect(domain).toContain('.');
    expect(domain.length).toBeGreaterThan(2);
  });

  /**
   * **Validates: Requirements 3.3**
   * Guest address postal code must be a valid UK postcode format.
   */
  test('GuestData.GUEST_ADDRESS postal code matches valid UK postcode format', () => {
    const address: GuestAddress = GuestData.GUEST_ADDRESS;

    expect(address.postalCode).toMatch(UK_POSTCODE_REGEX);
  });

  /**
   * **Validates: Requirements 3.3**
   * Guest address country code must be a 2-letter ISO code.
   */
  test('GuestData.GUEST_ADDRESS country code is a 2-letter ISO code', () => {
    const address: GuestAddress = GuestData.GUEST_ADDRESS;

    expect(address.countryCode).toHaveLength(2);
    expect(address.countryCode).toMatch(/^[A-Z]{2}$/);
  });

  /**
   * **Validates: Requirements 3.3**
   * All required address fields must be non-empty strings.
   */
  test('GuestData.GUEST_ADDRESS has all required address fields as non-empty strings', () => {
    const address: GuestAddress = GuestData.GUEST_ADDRESS;

    expect(address.addressType).toBeTruthy();
    expect(['HOME', 'BUSINESS']).toContain(address.addressType);

    expect(address.postalCode).toBeTruthy();
    expect(typeof address.postalCode).toBe('string');
    expect(address.postalCode.length).toBeGreaterThan(0);

    expect(address.addressLine1).toBeTruthy();
    expect(typeof address.addressLine1).toBe('string');
    expect(address.addressLine1.length).toBeGreaterThan(0);

    expect(address.cityName).toBeTruthy();
    expect(typeof address.cityName).toBe('string');
    expect(address.cityName.length).toBeGreaterThan(0);

    expect(address.countryCode).toBeTruthy();
    expect(typeof address.countryCode).toBe('string');
    expect(address.countryCode.length).toBeGreaterThan(0);
  });

  /**
   * **Validates: Requirements 3.2, 3.3**
   * GuestInfo and GuestAddress interfaces expose all fields needed by
   * the GuestDetailsPage form filling methods.
   */
  test('GuestInfo and GuestAddress data structures have all required fields for form filling', () => {
    const guest: GuestInfo = GuestData.GUEST_INFO;
    const address: GuestAddress = GuestData.GUEST_ADDRESS;

    // GuestInfo must have all fields used by fillBookerInformation
    expect(guest).toHaveProperty('title');
    expect(guest).toHaveProperty('firstName');
    expect(guest).toHaveProperty('lastName');
    expect(guest).toHaveProperty('emailAddress');
    expect(guest).toHaveProperty('mobile');
    expect(guest).toHaveProperty('landline');

    // GuestAddress must have all fields used by fillAddress
    expect(address).toHaveProperty('addressType');
    expect(address).toHaveProperty('postalCode');
    expect(address).toHaveProperty('addressLine1');
    expect(address).toHaveProperty('addressLine2');
    expect(address).toHaveProperty('addressLine3');
    expect(address).toHaveProperty('countryCode');
    expect(address).toHaveProperty('cityName');
  });
});
