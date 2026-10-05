/**
 * Basket status validator tests for the current QA API version.
 *
 * The latest model validates basket state directly on the Basket response object,
 * rather than through a removed GraphQL-only BasketAPI wrapper.
 */

import { test, expect } from '@playwright/test';
import { Basket } from '../../../src/api/response/basket';

const basketFixture = {
  createdAt: '2025-06-15T10:00:00.000Z',
  hotelId: 'GATGAT',
  itemTypes: ['STAY'],
  items: [
    {
      details: null,
      sourceId: 'RES-12345',
      type: 'STAY',
    },
  ],
  lastModifiedAt: '2025-06-15T10:05:00.000Z',
  lockingTime: '2025-06-15T10:30:00.000Z',
  paymentID: 'pay-abc-123',
  reference: 'GATGAT4995860',
  bookingReference: '4995860',
  status: 'COMPLETED',
  paymentStatus: 'COMPLETED',
  paymentOption: 'CREDIT_CARD',
  userId: null,
  sendMail: true,
} as const;

test.describe('Basket — fixture-based validation', () => {
  test.describe('validateBasketStatus', () => {
    test('passes when basket status matches expected', async () => {
      const basket = new Basket({ basket: basketFixture as Record<string, unknown> });

      await expect(basket.validateBasketStatus('COMPLETED')).resolves.toBeUndefined();
    });

    test('throws an error when basket status mismatches', async () => {
      const basket = new Basket({ basket: basketFixture as Record<string, unknown> });

      await expect(basket.validateBasketStatus('OPEN')).rejects.toThrow(/Basket status is not as expected/);
    });

    test('includes the mismatch context in the error message', async () => {
      const basket = new Basket({ basket: basketFixture as Record<string, unknown> });

      await expect(basket.validateBasketStatus('PROCESSING')).rejects.toThrow(/Basket status is not as expected/);
      await expect(basket.validateBasketStatus('PROCESSING')).rejects.toThrow(/COMPLETED/);
    });

    test('rejects empty expected status values', async () => {
      const basket = new Basket({ basket: basketFixture as Record<string, unknown> });

      await expect(basket.validateBasketStatus('' as string)).rejects.toThrow(/Basket status is not as expected/);
    });

    test('accepts OPEN status when the basket matches', async () => {
      const basket = new Basket({
        basket: { ...basketFixture, status: 'OPEN' } as Record<string, unknown>,
      });

      await expect(basket.validateBasketStatus('OPEN')).resolves.toBeUndefined();
    });

    test('accepts PAY_PENDING status when the basket matches', async () => {
      const basket = new Basket({
        basket: { ...basketFixture, status: 'PAY_PENDING' } as Record<string, unknown>,
      });

      await expect(basket.validateBasketStatus('PAY_PENDING')).resolves.toBeUndefined();
    });
  });
});
