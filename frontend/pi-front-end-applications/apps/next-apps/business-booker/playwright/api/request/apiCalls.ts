/* eslint-disable no-console */
/* eslint-disable prettier/prettier */
import { HotelRate, Constants } from '@WB-playwright/constants';
import { HotelAvailabilityInput } from '@WB-playwright/types';
import { format, addDays } from 'date-fns';

import { GraphQlUtils } from './graphqlCalls';

export const ApiCalls = {
  async getHotelInventoryAndLogRooms(hotelAvailabilityInput: HotelAvailabilityInput) {
    for (const room of hotelAvailabilityInput.rooms) {
      if (typeof room.roomType !== 'string' && room.roomType.id != undefined) {
        room.roomType.id;
      }
    }
    const hotelInventory = await GraphQlUtils.graphqlGetHotelInventory(
      hotelAvailabilityInput.hotelCode,
      hotelAvailabilityInput.arrival,
      hotelAvailabilityInput.departure
    );
    console.log(`Requested rooms: ${JSON.stringify(hotelAvailabilityInput.rooms)}`);
    console.log(`Available rooms: ${JSON.stringify(hotelInventory.roomTypeInventories)}`);
  },

  async getHotelAvailability(hotelAvailabilityInput: HotelAvailabilityInput, ratePlanCode = HotelRate.BUSINESS_FLEX.ratePlanCode,  pmsRoomType = '' ) {
    // number of retries to look for hotels availabilities over 3 months span (90 days)
    let retries = 10;
    let serviceErrorIndex = 1;
    let roomFound = false;

    let hotelAvailability = await GraphQlUtils.graphqlGetSingleHotelAvailability(hotelAvailabilityInput, false);
    while ((hotelAvailability.errors && hotelAvailability.errors[0].errorType === '500') || (!roomFound && retries !== 0) ) {
      let filteredRoomRates = hotelAvailability.errors ? [] : hotelAvailability.roomRates.filter(
            (roomRate: { ratePlanCode: string }) => !ratePlanCode || roomRate.ratePlanCode === ratePlanCode);

      filteredRoomRates = filteredRoomRates.filter((roomRate: { roomTypes: { rooms: any[] }[] }) => roomRate.roomTypes.filter((roomType: { rooms: any[] }) => roomType.rooms.length > 0 &&
              roomType.rooms.filter((room) => !pmsRoomType || room.pmsRoomType === pmsRoomType).length > 0).length >= hotelAvailabilityInput.rooms.length);

      const hasEnoughRooms = filteredRoomRates.length > 0;

      if (hotelAvailability.errors || hotelAvailability.available === false || !hasEnoughRooms) {
        if (hotelAvailability.errors) {
          if (serviceErrorIndex === 6) {
            throw new Error('PMS Adapter error, probably the service is down or request is invalid!');
          } else {
            console.log(`Service returned 500 Errors. Service errors count: ${serviceErrorIndex} / 5`);
            // add some delay between retries
            await new Promise((r) => setTimeout(r, 2000));
            serviceErrorIndex += 1;
          }
        } else {
          console.log( `No rooms are available for the period: ${hotelAvailabilityInput.arrival} - ${hotelAvailabilityInput.departure}`);
        }
        hotelAvailabilityInput.arrival = format(addDays(new Date(hotelAvailabilityInput.arrival), 1), Constants.ISO_DAY_DATE_FORMAT);
        hotelAvailabilityInput.departure = format(addDays(new Date(hotelAvailabilityInput.departure), 1), Constants.ISO_DAY_DATE_FORMAT);
        hotelAvailability = await GraphQlUtils.graphqlGetSingleHotelAvailability(hotelAvailabilityInput, false);
        retries -= 1;
        console.log(`Retries left: ${retries}`);
        if (retries === 0) {
          await this.getHotelInventoryAndLogRooms(hotelAvailabilityInput);
          throw new Error(`No rooms are available for the period: ${hotelAvailabilityInput.arrival} - ${hotelAvailabilityInput.departure}`);
        }
      } else {
        console.log(`Room found for the period: ${hotelAvailabilityInput.arrival} - ${hotelAvailabilityInput.departure}`);
        roomFound = true;
        hotelAvailability.ratePlanCode = ratePlanCode && ratePlanCode !== HotelRate.BUSINESS_FLEX.ratePlanCode ? ratePlanCode : filteredRoomRates[0].ratePlanCode;
        break;
      }
    }
    // set correct reservation dates for the hotelAvailability object
    hotelAvailability.startDate = hotelAvailabilityInput.arrival;
    hotelAvailability.endDate = hotelAvailabilityInput.departure;
    return hotelAvailability;
  },
};
