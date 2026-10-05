/**
 * Booking confirmation helpers — fixture-based regression tests.
 *
 * These tests target the current implementation in the QA API layer, which uses
 * the instance-based ApiBookingConfirmationHelpers API rather than the removed
 * GraphQL-only wrapper pattern from the old design.
 */

import { test, expect } from '@playwright/test';
import { ApiBookingConfirmationHelpers } from '../../../src/api/graphql/bookingConfirmationHelpers';
import fixture from './fixtures/booking-confirmation-fixture.json';

global.expect = expect;

const booking = fixture as Record<string, unknown>;

test.describe('ApiBookingConfirmationHelpers — fixture-based validation', () => {
  let helpers: ApiBookingConfirmationHelpers;

  test.beforeEach(() => {
    helpers = new ApiBookingConfirmationHelpers(booking);
  });

  test('validateHotel passes when hotel matches', async () => {
    await expect(helpers.validateHotel({ id: 'GATGAT', name: 'London Gatwick Airport', type: 'hotel', countryCode: 'GB' } as any)).resolves.toBeUndefined();
  });

  test('validateHotel throws when hotel id mismatches', async () => {
    await expect(helpers.validateHotel({ id: 'LONEUS', name: 'London Gatwick Airport', type: 'hotel', countryCode: 'GB' } as any)).rejects.toThrow();
  });

  test('validateHotel ignores hotel name mismatch when hotel id matches', async () => {
    await expect(helpers.validateHotel({ id: 'GATGAT', name: 'Wrong Hotel Name', type: 'hotel', countryCode: 'GB' } as any)).resolves.toBeUndefined();
  });

  test('validateStayingDates passes when dates match', async () => {
    await expect(helpers.validateStayingDates({ arrivalDate: new Date('2025-06-15'), departureDate: new Date('2025-06-17') } as any)).resolves.toBeUndefined();
  });

  test('validateStayingDates throws when arrival date is wrong', async () => {
    await expect(helpers.validateStayingDates({ arrivalDate: new Date('2025-07-01'), departureDate: new Date('2025-06-17') } as any)).rejects.toThrow();
  });

  test('validateRoomsOccupancy passes when occupancy matches', async () => {
    await expect(helpers.validateRoomsOccupancy({ rooms: [{ adultsNumber: 2, childrenNumber: 0 }] } as any)).resolves.toBeUndefined();
  });

  test('validateRoomsOccupancy throws when room count mismatches', async () => {
    await expect(helpers.validateRoomsOccupancy({ rooms: [{ adultsNumber: 2, childrenNumber: 0 }, { adultsNumber: 2, childrenNumber: 1 }] } as any)).rejects.toThrow();
  });

  test('validateRoomTypes passes when room id matches', async () => {
    await expect(helpers.validateRoomTypes({ ids: ['DOUBLE'] }, 'PI')).resolves.toBeUndefined();
  });

  test('validateRoomTypes accepts the compact PMS code for a localized double room', async () => {
    const localizedDoubleBooking = JSON.parse(JSON.stringify(booking)) as Record<string, any>;
    localizedDoubleBooking.reservationByIdList[0].roomStay.roomType = 'DB';
    const localizedDoubleHelpers = new ApiBookingConfirmationHelpers(localizedDoubleBooking);

    await expect(localizedDoubleHelpers.validateRoomTypes({ ids: ['DOUBLE'] }, 'PI')).resolves.toBeUndefined();
  });

  test('validateRoomTypes throws when room id mismatches', async () => {
    await expect(helpers.validateRoomTypes({ ids: ['SINGLE'] }, 'PI')).rejects.toThrow();
  });

  test('validateRatePlan passes when rate plan matches', async () => {
    await expect(helpers.validateRatePlan('FLEXRATE')).resolves.toBeUndefined();
  });

  test('validateRatePlan throws when rate plan mismatches', async () => {
    await expect(helpers.validateRatePlan('NONFLEX')).rejects.toThrow();
  });

  test('validateRatesPerNight passes when nightly rates match', async () => {
    await expect(helpers.validateRatesPerNight([89.0, 95.0])).resolves.toBeUndefined();
  });

  test('validateRatesPerNight throws when nightly rates are wrong', async () => {
    await expect(helpers.validateRatesPerNight([100.0, 95.0])).rejects.toThrow();
  });

  test('validateRoomPrice passes when room price matches', async () => {
    await expect(helpers.validateRoomPrice([{ totalPrice: 184.0 }])).resolves.toBeUndefined();
  });

  test('validateRoomPrice throws when total price is wrong', async () => {
    await expect(helpers.validateRoomPrice([{ totalPrice: 200.0 }])).rejects.toThrow();
  });

  test('validateCurrency passes when currency matches', async () => {
    await expect(helpers.validateCurrency('GBP')).resolves.toBeUndefined();
  });

  test('validateCurrency throws when currency mismatches', async () => {
    await expect(helpers.validateCurrency('EUR')).rejects.toThrow();
  });

  test('validateBookingFlowId passes when booking flow matches', async () => {
    await expect(helpers.validateBookingFlowId('flow-123-abc')).resolves.toBeUndefined();
  });

  test('validateBookingFlowId throws when booking flow mismatches', async () => {
    await expect(helpers.validateBookingFlowId('wrong-flow-id')).rejects.toThrow();
  });

  test('validateDepositPoliciesForAllRooms passes when policy matches', async () => {
    await expect(helpers.validateDepositPoliciesForAllRooms('NON')).resolves.toBeUndefined();
  });

  test('validateDepositPoliciesForAllRooms throws when policy mismatches', async () => {
    await expect(helpers.validateDepositPoliciesForAllRooms('DAX')).rejects.toThrow();
  });

  test('validateTotalCostNoDiscountsNoAmendment passes when total cost matches', async () => {
    await expect(helpers.validateTotalCostNoDiscountsNoAmendment(184.0, 'CREDIT_CARD')).resolves.toBeUndefined();
  });

  test('validateTotalCostNoDiscountsNoAmendment throws when total cost is wrong', async () => {
    await expect(helpers.validateTotalCostNoDiscountsNoAmendment(200.0, 'CREDIT_CARD')).rejects.toThrow();
  });

  test('validatePaymentCard passes when card digits match', async () => {
    await expect(helpers.validatePaymentCard('PAY_ON_ARRIVAL', '1234')).resolves.toBeUndefined();
  });

  test('validatePaymentCard throws when card digits mismatch', async () => {
    await expect(helpers.validatePaymentCard('PAY_ON_ARRIVAL', '9999')).rejects.toThrow();
  });

  test('validateCityTax passes when city tax is zero and expected false', async () => {
    await expect(helpers.validateCityTax(false)).resolves.toBeUndefined();
  });

  test('validateCityTax throws when expected true but tax is zero', async () => {
    await expect(helpers.validateCityTax(true)).rejects.toThrow();
  });

  test('validateGuestDetailsAgainstBookingConfirmation passes when guest details match', async () => {
    await expect(
      helpers.validateGuestDetailsAgainstBookingConfirmation({
        guestDetails: {
          booker: {
            title: 'Mr',
            firstName: 'John',
            lastName: 'Smith',
            emailAddress: 'john.smith@example.com',
            mobile: '+447700900000',
          },
          reasonForStay: 'LEI',
        } as any,
        stayingGuestsAndRoomDetails: [{ firstName: 'John', adultsNumber: 2, childrenNumber: 0, roomType: 'double' }] as any,
      } as any),
    ).resolves.toBeUndefined();
  });

  test('validateGuests normalizes guest name spacing and case', async () => {
    await expect(helpers.validateGuests([{
      firstName: '  JOHN ',
      lastName: '  smith  ',
      adultsNumber: 2,
      childrenNumber: 0,
    }] as any), 'Guest names with different spacing and case should match').resolves.toBeUndefined();
  });

  test('validateGuestDetailsAgainstBookingConfirmation throws when guest first name mismatches', async () => {
    await expect(
      helpers.validateGuestDetailsAgainstBookingConfirmation({
        guestDetails: {
          booker: {
            title: 'Mr',
            firstName: 'John',
            lastName: 'Smith',
            emailAddress: 'john.smith@example.com',
            mobile: '+447700900000',
          },
          reasonForStay: 'LEI',
        } as any,
        stayingGuestsAndRoomDetails: [{ firstName: 'Jane', adultsNumber: 2, childrenNumber: 0, roomType: 'double' }] as any,
      } as any),
    ).rejects.toThrow();
  });
});
