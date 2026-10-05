import { test } from '@playwright/test';
import { getEnvironmentConfig } from '../../../config/environments';
import { GuestData } from '@test-data/guestData';
import { Hotels } from '@test-data/hotels';
import type { BaseOptions } from '../../../src/fixtures/base.fixture';

/**
 * API Availability Check
 *
 * Verifies that the target hotel has availability for the configured dates
 * before running the E2E booking flow. This test uses the GraphQL API directly
 * (no browser needed) and fails fast if rooms aren't available.
 */
test.describe('Hotel Availability API Check', () => {
  test('Hotels.DEFAULT_HOTEL has Flex rate availability for check-in/check-out dates', async ({ request }, testInfo) => {
    const projectUse = testInfo.project.use as typeof testInfo.project.use & { options?: BaseOptions['options'] };
    const options = projectUse.options ?? { app: 'pi' };
    const env = getEnvironmentConfig(options);

    const query = `
      query hotelAvailability(
        $hotelId: String!
        $arrival: String!
        $departure: String!
        $rooms: [RoomSearch!]!
        $bookingChannel: BookingChannelCriteria!
        $promotionCode: String
      ) {
        hotelAvailability(
          availabilitySearchCriteria: {
            arrival: $arrival
            departure: $departure
            rooms: $rooms
            hotel: { identifier: $hotelId }
            bookingChannel: $bookingChannel
            promotionCode: $promotionCode
          }
        ) {
          hotelId
          startDate
          endDate
          available
          limitedAvailability
          roomRates {
            ratePlanCode
            roomTypes {
              roomType
              roomNumber
              adults
              children
              rooms {
                roomType
                pmsRoomType
                roomClass
                roomPriceBreakdown {
                  totalNetAmount
                  currencyCode
                }
                numberOfRoomsAvailable
              }
            }
          }
        }
      }
    `;

    const variables = {
      hotelId: Hotels.DEFAULT_HOTEL.id,
      arrival: GuestData.CHECK_IN_DATE,
      departure: GuestData.CHECK_OUT_DATE,
      rooms: [{ adultsNumber: 1, childrenNumber: 0, cotRequired: false, roomType: 'DB' }],
      bookingChannel: {
        channel: 'PI',
        subchannel: 'WEB',
        language: 'EN',
      },
      promotionCode: null,
    };

    // --- Step 1: Call the API ---
    const response = await test.step(
      `Calling availability API for "${Hotels.DEFAULT_HOTEL.name}" on ${GuestData.CHECK_IN_DATE} to ${GuestData.CHECK_OUT_DATE}`,
      async () => {
        return request.post(env.apiBaseUrl, {
          headers: {
            'Content-Type': 'application/json',
          },
          data: { query, variables },
        });
      }
    );

    // --- Step 2: Verify API responded successfully ---
    const body: any = await test.step('Verified that the availability API returned a successful response', async () => {
      if (response.status() !== 200) {
        throw new Error(`API returned HTTP ${response.status()} instead of 200`);
      }
      const json = await response.json();
      if (json.errors && !json.data?.hotelAvailability) {
        throw new Error(`API returned errors: ${JSON.stringify(json.errors).substring(0, 300)}`);
      }
      return json;
    });

    // --- Step 3: Verify hotel is available ---
    const availability = await test.step(
      `Verified that "${Hotels.DEFAULT_HOTEL.name}" has availability on ${GuestData.CHECK_IN_DATE}`,
      async () => {
        const avail = body.data?.hotelAvailability;
        if (!avail) {
          throw new Error('No availability data returned from API');
        }
        if (!avail.available) {
          throw new Error(`Hotel "${Hotels.DEFAULT_HOTEL.name}" is NOT available for ${GuestData.CHECK_IN_DATE} to ${GuestData.CHECK_OUT_DATE}`);
        }
        return avail;
      }
    );

    // --- Step 4: Verify Flex rate exists ---
    const flexRate: any = await test.step('Verified that a Flex rate plan is offered for this hotel and date', async () => {
      if (!availability.roomRates || availability.roomRates.length === 0) {
        throw new Error('No rate plans returned in the availability response');
      }
      const allRateCodes = availability.roomRates.map((r: { ratePlanCode: string }) => r.ratePlanCode);
      const flex = availability.roomRates.find(
        (rate: { ratePlanCode: string }) => rate.ratePlanCode.toUpperCase().includes('FLEX')
      );
      if (!flex) {
        throw new Error(`No Flex rate found. Available rates: [${allRateCodes.join(', ')}]`);
      }
      return flex;
    });

    // --- Step 5: Verify rooms are bookable ---
    await test.step('Verified that at least one room is available to book at the Flex rate', async () => {
      const rooms = flexRate.roomTypes?.[0]?.rooms;
      if (!rooms || rooms.length === 0) {
        throw new Error('No rooms listed under the Flex rate');
      }
      const availableRoom = rooms.find((r: { numberOfRoomsAvailable: number }) => r.numberOfRoomsAvailable > 0);
      if (!availableRoom) {
        throw new Error('All rooms under the Flex rate show 0 availability');
      }
      console.log(`Room class: ${availableRoom.roomClass}, Available: ${availableRoom.numberOfRoomsAvailable}, Price: ${availableRoom.roomPriceBreakdown.currencyCode} ${availableRoom.roomPriceBreakdown.totalNetAmount}`);
    });
  });
});
