import {
  RateClassificationNames,
  HIAEMroomTypesInfo,
  HIAvailabilityRates,
} from '@whitbread-eos/api';

import { formatCurrency } from '../formatters';

export function containsPlaceLD(
  hotelAvailability: HIAvailabilityRates,
  roomTypeInformation: HIAEMroomTypesInfo,
  hotelBrand: string
) {
  const roomTypeLookup: { [key: string]: any } = {};

  roomTypeInformation?.roomTypeInformation?.roomTypes.forEach((roomInfo: any) => {
    roomInfo?.roomTypeCode.forEach((code: string) => {
      roomTypeLookup[code] = roomInfo;
    });
  });

  const groupedRooms: { [key: string]: any } = {};

  hotelAvailability?.hotelAvailability?.roomRates.forEach((ratePlan: any) => {
    ratePlan.roomTypes.forEach((roomTypeData: any) => {
      roomTypeData.rooms.forEach((room: any) => {
        const roomKey = room.pmsRoomType;
        const matchedRoomInfo = roomTypeLookup[room.pmsRoomType];

        if (!groupedRooms[roomKey]) {
          groupedRooms[roomKey] = {
            '@type': ['HotelRoom', 'Product'],

            name: matchedRoomInfo?.roomLabel,

            description: matchedRoomInfo?.roomDescription,

            image: matchedRoomInfo?.roomImage
              ? `https://www.premierinn.com${matchedRoomInfo.roomImage}`
              : null,

            inventoryLevel: {
              '@type': 'QuantitativeValue',
              value: room.numberOfRoomsAvailable || null,
              description: `Only ${room.numberOfRoomsAvailable} room(s) left for your search`,
            },

            occupancy: {
              '@type': 'QuantitativeValue',
              value: (roomTypeData.adults || 0) + (roomTypeData.children || 0),
            },

            offers: [],
          };
        }

        groupedRooms[roomKey].offers.push({
          '@type': ['Offer', 'LodgingReservation'],

          name:
            RateClassificationNames[
              ratePlan.ratePlanCode as keyof typeof RateClassificationNames
            ] ||
            ratePlan.ratePlanCode ||
            null,

          checkinTime:
            hotelBrand === 'PID'
              ? `${hotelAvailability?.hotelAvailability?.startDate}T15:00:00`
              : `${hotelAvailability?.hotelAvailability?.startDate}T14:00:00`,

          checkoutTime: `${hotelAvailability?.hotelAvailability?.endDate}T12:00:00`,
          price: room.roomPriceBreakdown?.totalNetAmount ?? null,

          priceCurrency: room.roomPriceBreakdown?.currencyCode ?? null,

          availability: hotelAvailability?.hotelAvailability?.available
            ? 'https://schema.org/InStock'
            : 'https://schema.org/OutOfStock',

          priceSpecification: {
            '@type': 'CompoundPriceSpecification',

            price: room.roomPriceBreakdown?.totalNetAmount ?? null,

            priceCurrency: room.roomPriceBreakdown?.currencyCode ?? null,

            unitCode: 'DAY',
          },
        });
      });
    });
  });

  return Object.values(groupedRooms);
}

export function getPriceRange(dataHotelAvailabilityPI: HIAvailabilityRates) {
  const prices = [] as number[];

  const { currencyCode } =
    dataHotelAvailabilityPI?.hotelAvailability?.roomRates?.[0]?.roomTypes?.[0]?.rooms?.[0]
      ?.roomPriceBreakdown || {};

  dataHotelAvailabilityPI?.hotelAvailability?.roomRates?.forEach((ratePlan) => {
    ratePlan?.roomTypes?.forEach((roomType) => {
      roomType?.rooms?.forEach((room) => {
        const price = room?.roomPriceBreakdown?.totalNetAmount;

        if (typeof price === 'number') {
          prices.push(price);
        }
      });
    });
  });

  if (!prices.length) {
    return null;
  }

  const min = Math.min(...prices);
  const max = Math.max(...prices);

  const currency = formatCurrency(currencyCode as string);

  return `${currency}${min} - ${currency}${max}`;
}
