/* eslint-disable prettier/prettier */
import { config } from '@WB-playwright/config';
import {
  Constants,
  RoomComposition,
  createRoom,
  createRate,
  getHotelRateByRatePlanCode,
  Locations,
  HotelRate,
} from '@WB-playwright/constants';
import { BookingChannel, HotelAvailabilityInput, Room, SearchCriteria } from '@WB-playwright/types';
import { format, addDays, parseISO } from 'date-fns';

import { ApiCalls } from '../api/request/apiCalls';

export const createSearchCriteria = ({
  arrivalDate = new Date(),
  nights = 1,
  location = Locations.London.name,
  rate = createRate(HotelRate.BUSINESS_FLEX),
  rooms = [createRoom()],
}: Partial<SearchCriteria> = {}): SearchCriteria => {
  const departureDate = addDays(arrivalDate, 1);
  return { arrivalDate, departureDate, nights, location, rate, rooms };
};

export const createBookingChannel = (
  channel = 'BB',
  subchannel = 'WEB',
  language = config.LANGUAGE.toUpperCase()
): BookingChannel => {
  return { channel, subchannel, language };
};

export const createHotelAvailabilityInputWithInterval = async (
  hotelId: string,
  daysNumberForArrivalDate: number,
  daysNumber: number,
  rooms: Room[] = [RoomComposition.DOUBLE_1_ADULT_0_CHILDREN],
  bookingChannel = createBookingChannel()
): Promise<HotelAvailabilityInput> => {
  const arrival = format(
    addDays(new Date(), daysNumberForArrivalDate),
    Constants.ISO_DAY_DATE_FORMAT
  );
  const departure = format(
    addDays(new Date(), daysNumberForArrivalDate + daysNumber),
    Constants.ISO_DAY_DATE_FORMAT
  );
  return {
    hotelCode: hotelId,
    arrival,
    departure,
    rooms,
    bookingChannel,
  };
};

export async function getSearchCriteriaAndHotelAvailability({
  hotelLocation,
  hotelId,
  daysNumberForArrivalDate = 2,
  daysNumber = 1,
  rooms = [RoomComposition.DOUBLE_1_ADULT_0_CHILDREN],
  ratePlanCode = '',
  pmsRoomType = '',
}: {
  hotelLocation: string;
  hotelId: string;
  daysNumberForArrivalDate?: number;
  daysNumber?: number;
  rooms?: Room[];
  ratePlanCode?: string;
  pmsRoomType?: string;
}) {
  const hotelAvailabilityInput = createHotelAvailabilityInputWithInterval(
    hotelId,
    daysNumberForArrivalDate,
    daysNumber,
    rooms
  );
  const hotelAvailabilityResponse = await ApiCalls.getHotelAvailability(
    await hotelAvailabilityInput,
    ratePlanCode,
    pmsRoomType
  );
  const hotelRate = ratePlanCode ? ratePlanCode : hotelAvailabilityResponse.ratePlanCode;
  const roomsList: Room[] = [];
  for (let index = 0; index < rooms.length; index++) {
    const room = createRoom();
    roomsList.push(room);
    roomsList[index].adultsNumber = rooms[index].adultsNumber;
    roomsList[index].childrenNumber = rooms[index].childrenNumber;
    roomsList[index].cotRequired = rooms[index].cotRequired;
    roomsList[index].roomType = rooms[index].roomType;
    roomsList[index].roomNumber = index + 1;
  }
  const searchCriteria = createSearchCriteria({
    arrivalDate: parseISO((await hotelAvailabilityInput).arrival),
    departureDate: parseISO((await hotelAvailabilityInput).departure),
    nights: daysNumber,
    location: hotelLocation,
    rate: await getHotelRateByRatePlanCode(hotelRate),
    rooms: roomsList,
  });
  return { hotelAvailabilityResponse, searchCriteria };
}

