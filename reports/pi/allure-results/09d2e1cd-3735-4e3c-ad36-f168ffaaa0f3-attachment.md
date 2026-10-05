# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: regressions/api/booking-confirmation.spec.ts >> ApiBookingConfirmationHelpers — fixture-based validation >> validateRoomTypes accepts the compact PMS code for a localized double room
- Location: qa/tests/regressions/api/booking-confirmation.spec.ts:56:7

# Error details

```
Error: expect(received).resolves.toBeUndefined()

Received promise rejected instead of resolved
Rejected to value: [Error: double room type name is not found in the list!]
```

# Test source

```ts
  1   | /**
  2   |  * Booking confirmation helpers — fixture-based regression tests.
  3   |  *
  4   |  * These tests target the current implementation in the QA API layer, which uses
  5   |  * the instance-based ApiBookingConfirmationHelpers API rather than the removed
  6   |  * GraphQL-only wrapper pattern from the old design.
  7   |  */
  8   | 
  9   | import { test, expect } from '@playwright/test';
  10  | import { ApiBookingConfirmationHelpers } from '../../../src/api/graphql/bookingConfirmationHelpers';
  11  | import fixture from './fixtures/booking-confirmation-fixture.json';
  12  | 
  13  | global.expect = expect;
  14  | 
  15  | const booking = fixture as Record<string, unknown>;
  16  | 
  17  | test.describe('ApiBookingConfirmationHelpers — fixture-based validation', () => {
  18  |   let helpers: ApiBookingConfirmationHelpers;
  19  | 
  20  |   test.beforeEach(() => {
  21  |     helpers = new ApiBookingConfirmationHelpers(booking);
  22  |   });
  23  | 
  24  |   test('validateHotel passes when hotel matches', async () => {
  25  |     await expect(helpers.validateHotel({ id: 'GATGAT', name: 'London Gatwick Airport', type: 'hotel', countryCode: 'GB' } as any)).resolves.toBeUndefined();
  26  |   });
  27  | 
  28  |   test('validateHotel throws when hotel id mismatches', async () => {
  29  |     await expect(helpers.validateHotel({ id: 'LONEUS', name: 'London Gatwick Airport', type: 'hotel', countryCode: 'GB' } as any)).rejects.toThrow();
  30  |   });
  31  | 
  32  |   test('validateHotel ignores hotel name mismatch when hotel id matches', async () => {
  33  |     await expect(helpers.validateHotel({ id: 'GATGAT', name: 'Wrong Hotel Name', type: 'hotel', countryCode: 'GB' } as any)).resolves.toBeUndefined();
  34  |   });
  35  | 
  36  |   test('validateStayingDates passes when dates match', async () => {
  37  |     await expect(helpers.validateStayingDates({ arrivalDate: new Date('2025-06-15'), departureDate: new Date('2025-06-17') } as any)).resolves.toBeUndefined();
  38  |   });
  39  | 
  40  |   test('validateStayingDates throws when arrival date is wrong', async () => {
  41  |     await expect(helpers.validateStayingDates({ arrivalDate: new Date('2025-07-01'), departureDate: new Date('2025-06-17') } as any)).rejects.toThrow();
  42  |   });
  43  | 
  44  |   test('validateRoomsOccupancy passes when occupancy matches', async () => {
  45  |     await expect(helpers.validateRoomsOccupancy({ rooms: [{ adultsNumber: 2, childrenNumber: 0 }] } as any)).resolves.toBeUndefined();
  46  |   });
  47  | 
  48  |   test('validateRoomsOccupancy throws when room count mismatches', async () => {
  49  |     await expect(helpers.validateRoomsOccupancy({ rooms: [{ adultsNumber: 2, childrenNumber: 0 }, { adultsNumber: 2, childrenNumber: 1 }] } as any)).rejects.toThrow();
  50  |   });
  51  | 
  52  |   test('validateRoomTypes passes when room id matches', async () => {
  53  |     await expect(helpers.validateRoomTypes({ ids: ['DOUBLE'] }, 'PI')).resolves.toBeUndefined();
  54  |   });
  55  | 
  56  |   test('validateRoomTypes accepts the compact PMS code for a localized double room', async () => {
  57  |     const localizedDoubleBooking = JSON.parse(JSON.stringify(booking)) as Record<string, any>;
  58  |     localizedDoubleBooking.reservationByIdList[0].roomStay.roomType = 'DB';
  59  |     const localizedDoubleHelpers = new ApiBookingConfirmationHelpers(localizedDoubleBooking);
  60  | 
> 61  |     await expect(localizedDoubleHelpers.validateRoomTypes('double', 'PI')).resolves.toBeUndefined();
      |                                                                                     ^ Error: expect(received).resolves.toBeUndefined()
  62  |   });
  63  | 
  64  |   test('validateRoomTypes throws when room id mismatches', async () => {
  65  |     await expect(helpers.validateRoomTypes({ ids: ['SINGLE'] }, 'PI')).rejects.toThrow();
  66  |   });
  67  | 
  68  |   test('validateRatePlan passes when rate plan matches', async () => {
  69  |     await expect(helpers.validateRatePlan('FLEXRATE')).resolves.toBeUndefined();
  70  |   });
  71  | 
  72  |   test('validateRatePlan throws when rate plan mismatches', async () => {
  73  |     await expect(helpers.validateRatePlan('NONFLEX')).rejects.toThrow();
  74  |   });
  75  | 
  76  |   test('validateRatesPerNight passes when nightly rates match', async () => {
  77  |     await expect(helpers.validateRatesPerNight([89.0, 95.0])).resolves.toBeUndefined();
  78  |   });
  79  | 
  80  |   test('validateRatesPerNight throws when nightly rates are wrong', async () => {
  81  |     await expect(helpers.validateRatesPerNight([100.0, 95.0])).rejects.toThrow();
  82  |   });
  83  | 
  84  |   test('validateRoomPrice passes when room price matches', async () => {
  85  |     await expect(helpers.validateRoomPrice([{ totalPrice: 184.0 }])).resolves.toBeUndefined();
  86  |   });
  87  | 
  88  |   test('validateRoomPrice throws when total price is wrong', async () => {
  89  |     await expect(helpers.validateRoomPrice([{ totalPrice: 200.0 }])).rejects.toThrow();
  90  |   });
  91  | 
  92  |   test('validateCurrency passes when currency matches', async () => {
  93  |     await expect(helpers.validateCurrency('GBP')).resolves.toBeUndefined();
  94  |   });
  95  | 
  96  |   test('validateCurrency throws when currency mismatches', async () => {
  97  |     await expect(helpers.validateCurrency('EUR')).rejects.toThrow();
  98  |   });
  99  | 
  100 |   test('validateBookingFlowId passes when booking flow matches', async () => {
  101 |     await expect(helpers.validateBookingFlowId('flow-123-abc')).resolves.toBeUndefined();
  102 |   });
  103 | 
  104 |   test('validateBookingFlowId throws when booking flow mismatches', async () => {
  105 |     await expect(helpers.validateBookingFlowId('wrong-flow-id')).rejects.toThrow();
  106 |   });
  107 | 
  108 |   test('validateDepositPoliciesForAllRooms passes when policy matches', async () => {
  109 |     await expect(helpers.validateDepositPoliciesForAllRooms('NON')).resolves.toBeUndefined();
  110 |   });
  111 | 
  112 |   test('validateDepositPoliciesForAllRooms throws when policy mismatches', async () => {
  113 |     await expect(helpers.validateDepositPoliciesForAllRooms('DAX')).rejects.toThrow();
  114 |   });
  115 | 
  116 |   test('validateTotalCostNoDiscountsNoAmendment passes when total cost matches', async () => {
  117 |     await expect(helpers.validateTotalCostNoDiscountsNoAmendment(184.0, 'CREDIT_CARD')).resolves.toBeUndefined();
  118 |   });
  119 | 
  120 |   test('validateTotalCostNoDiscountsNoAmendment throws when total cost is wrong', async () => {
  121 |     await expect(helpers.validateTotalCostNoDiscountsNoAmendment(200.0, 'CREDIT_CARD')).rejects.toThrow();
  122 |   });
  123 | 
  124 |   test('validatePaymentCard passes when card digits match', async () => {
  125 |     await expect(helpers.validatePaymentCard('PAY_ON_ARRIVAL', '1234')).resolves.toBeUndefined();
  126 |   });
  127 | 
  128 |   test('validatePaymentCard throws when card digits mismatch', async () => {
  129 |     await expect(helpers.validatePaymentCard('PAY_ON_ARRIVAL', '9999')).rejects.toThrow();
  130 |   });
  131 | 
  132 |   test('validateCityTax passes when city tax is zero and expected false', async () => {
  133 |     await expect(helpers.validateCityTax(false)).resolves.toBeUndefined();
  134 |   });
  135 | 
  136 |   test('validateCityTax throws when expected true but tax is zero', async () => {
  137 |     await expect(helpers.validateCityTax(true)).rejects.toThrow();
  138 |   });
  139 | 
  140 |   test('validateGuestDetailsAgainstBookingConfirmation passes when guest details match', async () => {
  141 |     await expect(
  142 |       helpers.validateGuestDetailsAgainstBookingConfirmation({
  143 |         guestDetails: {
  144 |           booker: {
  145 |             title: 'Mr',
  146 |             firstName: 'John',
  147 |             lastName: 'Smith',
  148 |             emailAddress: 'john.smith@example.com',
  149 |             mobile: '+447700900000',
  150 |           },
  151 |           reasonForStay: 'LEI',
  152 |         } as any,
  153 |         stayingGuestsAndRoomDetails: [{ firstName: 'John', adultsNumber: 2, childrenNumber: 0, roomType: 'double' }] as any,
  154 |       } as any),
  155 |     ).resolves.toBeUndefined();
  156 |   });
  157 | 
  158 |   test('validateGuestDetailsAgainstBookingConfirmation throws when guest first name mismatches', async () => {
  159 |     await expect(
  160 |       helpers.validateGuestDetailsAgainstBookingConfirmation({
  161 |         guestDetails: {
```