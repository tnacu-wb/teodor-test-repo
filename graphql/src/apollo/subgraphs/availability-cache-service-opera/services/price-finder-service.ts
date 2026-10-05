import { addFieldsToMap, joinCriteria } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { PriceFinderLocationSearchCriteria } from '../models/price-finder-location-search-criteria';
import { fetchHotelInformation } from '../../content-entity-service/services/hotel-information-service';

async function addDetailsPageLinks(
  priceFinderList: any[],
  country: string,
  language: string,
  context: any
): Promise<any[]> {
  return Promise.all(
    priceFinderList.map(async ({ hotelCode, hotelName, availabilities }) => {
      let detailsPage = null;
      try {
        const hotelInfo: any = await fetchHotelInformation(
          {
            hotelId: hotelCode,
            country,
            language
          },
          context
        );
        detailsPage = hotelInfo?.links?.detailsPage || null;
      } catch (e) {
        console.error('ERROR: ', e);
      }
      return {
        hotelCode,
        hotelName,
        links: {
          detailsPage
        },
        availabilities: availabilities ?? []
      };
    })
  );
}

export const getLowestRatesByLocationId = async (
  { criteria }: { criteria: PriceFinderLocationSearchCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'locationId', value: criteria.locationId, required: true },
      { key: 'arrival', value: criteria.arrival, required: true },
      { key: 'daysRange', value: criteria.daysRange, required: true },
      { key: 'showMinimumNights', value: criteria.showMinimumNights, required: false },
      { key: 'page', value: criteria.page, required: true },
      { key: 'initialPageSize', value: criteria.initialPageSize, required: true },
      { key: 'lazyLoadPageSize', value: criteria.lazyLoadPageSize, required: true },
      { key: 'sortBy', value: criteria.sortBy, required: false },
      { key: 'sortDate', value: criteria.sortDate, required: false },
      { key: 'country', value: criteria.country, required: false },
      { key: 'language', value: criteria.language, required: false },
      {
        key: 'filterByRoomType',
        value: Array.isArray(criteria.filterByRoomType)
          ? criteria.filterByRoomType.join(',')
          : criteria.filterByRoomType,
        required: false
      }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    const response = await get(
      endpoints.PRICE_FINDER_BY_LOCATION,
      getLowestRatesByLocationId,
      finalMap,
      context
    );

    if (response?.priceFinderOperaHotelAvailabilitiesDtoList) {
      response.priceFinderOperaHotelAvailabilitiesDtoList = await addDetailsPageLinks(
        response.priceFinderOperaHotelAvailabilitiesDtoList,
        criteria.country,
        criteria.language,
        context
      );
    }
    return response;
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getLowestPricesByLocationForCalendar = async (
  {
    locationId,
    milesRadius,
    month
  }: {
    locationId: string;
    milesRadius: number;
    month: number;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'locationId', value: locationId, required: true },
      { key: 'milesRadius', value: milesRadius, required: true },
      { key: 'month', value: month, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(
      endpoints.PRICE_FINDER_BY_LOCATION_FOR_CALENDAR,
      getLowestPricesByLocationForCalendar,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const getLowestRatesByHotel = async (
  {
    hotelCodes,
    rooms,
    arrival,
    country,
    language,
    showMinimumNights
  }: {
    hotelCodes: string[];
    rooms: number;
    arrival: string;
    country: string;
    language: string;
    showMinimumNights: boolean;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'hotelCodes', value: joinCriteria(hotelCodes, '', ','), required: true },
      { key: 'rooms', value: rooms, required: false },
      { key: 'arrival', value: arrival, required: true },
      { key: 'country', value: country, required: false },
      { key: 'language', value: language, required: false },
      { key: 'showMinimumNights', value: showMinimumNights, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.PRICE_FINDER_BY_HOTEL, getLowestRatesByHotel, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
