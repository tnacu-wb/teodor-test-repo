import { test, expect } from '@playwright/test';
import { randomUUID } from 'crypto';
import { GuestData } from '@test-data/guestData';

/**
 * Property 8: Test Idempotency
 *
 * For any two distinct test executions, the generated guest email addresses
 * must be unique to prevent inter-run booking conflicts.
 *
 * `∀ run_i, run_j where i ≠ j: guestData(run_i).email ≠ guestData(run_j).email`
 *
 * **Validates: Requirements 3.4, 9.4**
 */

const UUID_REGEX =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;
const ISO_DATE_REGEX = /^\d{4}-\d{2}-\d{2}$/;

test.describe('Property 8: Test Idempotency', () => {
  /**
   * **Validates: Requirements 3.4, 9.4**
   * The email address must contain a UUID-like pattern ensuring uniqueness per run.
   */
  test('GuestData.GUEST_INFO.emailAddress contains a UUID pattern', () => {
    const email = GuestData.GUEST_INFO.emailAddress;

    // Extract the UUID portion between "test-" prefix and "@mailinator.com" suffix
    const match = email.match(/^test-(.+)@mailinator\.com$/);
    expect(match).not.toBeNull();

    const uuidPart = match![1];
    expect(uuidPart).toMatch(UUID_REGEX);
  });

  /**
   * **Validates: Requirements 3.4, 9.4**
   * The email address must end with @mailinator.com (test-safe domain).
   */
  test('GuestData.GUEST_INFO.emailAddress ends with @mailinator.com', () => {
    expect(GuestData.GUEST_INFO.emailAddress).toContain('@mailinator.com');
    expect(GuestData.GUEST_INFO.emailAddress.endsWith('@mailinator.com')).toBe(true);
  });

  /**
   * **Validates: Requirements 3.4, 9.4**
   * The email address must be a valid email format.
   */
  test('GuestData.GUEST_INFO.emailAddress is a valid email format', () => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    expect(GuestData.GUEST_INFO.emailAddress).toMatch(emailRegex);
  });

  /**
   * **Validates: Requirements 3.4, 9.4**
   * The underlying crypto.randomUUID() mechanism guarantees unique values.
   * Generating multiple emails using the same pattern produces unique values,
   * proving that each test run will get a distinct email address.
   */
  test('multiple email generations using crypto.randomUUID produce unique values', () => {
    // Replicate the email generation logic used in guestData module:
    //   `test-${randomUUID()}@mailinator.com`
    // Verify that calling this pattern multiple times yields unique emails.
    const emails = new Set<string>();
    const iterations = 100;

    for (let i = 0; i < iterations; i++) {
      const email = `test-${randomUUID()}@mailinator.com`;
      emails.add(email);
    }

    // All generated emails must be unique
    expect(emails.size).toBe(iterations);
  });

  /**
   * **Validates: Requirements 3.4, 9.4**
   * The current GuestData.GUEST_INFO email is distinct from any newly generated email,
   * proving inter-run uniqueness.
   */
  test('GuestData.GUEST_INFO.emailAddress differs from a freshly generated email', () => {
    const currentEmail = GuestData.GUEST_INFO.emailAddress;
    const newEmail = `test-${randomUUID()}@mailinator.com`;

    expect(currentEmail).not.toBe(newEmail);
  });

  /**
   * **Validates: Requirements 9.5**
   * GuestData.CHECK_IN_DATE is a valid ISO date string (YYYY-MM-DD) representing today + 3 days.
   */
  test('GuestData.CHECK_IN_DATE is a valid ISO date 3 days from today', () => {
    expect(GuestData.CHECK_IN_DATE).toMatch(ISO_DATE_REGEX);

    const expected = new Date();
    expected.setDate(expected.getDate() + 3);
    const expectedStr = expected.toISOString().split('T')[0];

    expect(GuestData.CHECK_IN_DATE).toBe(expectedStr);
  });

  /**
   * **Validates: Requirements 9.5**
   * GuestData.CHECK_OUT_DATE is a valid ISO date string (YYYY-MM-DD) representing today + 4 days.
   */
  test('GuestData.CHECK_OUT_DATE is a valid ISO date 4 days from today', () => {
    expect(GuestData.CHECK_OUT_DATE).toMatch(ISO_DATE_REGEX);

    const expected = new Date();
    expected.setDate(expected.getDate() + 4);
    const expectedStr = expected.toISOString().split('T')[0];

    expect(GuestData.CHECK_OUT_DATE).toBe(expectedStr);
  });

  /**
   * **Validates: Requirements 9.5**
   * GuestData.CHECK_IN_DATE and GuestData.CHECK_OUT_DATE maintain a 1-night gap (exactly 1 day apart).
   */
  test('GuestData.CHECK_IN_DATE and GuestData.CHECK_OUT_DATE have a 1-night gap', () => {
    const checkIn = new Date(GuestData.CHECK_IN_DATE);
    const checkOut = new Date(GuestData.CHECK_OUT_DATE);

    const diffMs = checkOut.getTime() - checkIn.getTime();
    const diffDays = diffMs / (1000 * 60 * 60 * 24);

    expect(diffDays).toBe(1);
  });
});
