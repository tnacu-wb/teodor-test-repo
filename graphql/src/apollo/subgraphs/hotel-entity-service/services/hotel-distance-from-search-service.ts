import { endpoints } from './base-service';
import { HotelDistanceFromSearchCriteria } from '../models/hotel-distance-from-search-criteria';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import {
  addFieldsToMap,
  getServiceEndpoint,
  objectIsNullEmptyOrUndefined,
  validateInteger
} from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the hotel distance from search
 *
 * @param hotelDistanceFromSearchCriteria
 * @param context
 */

export const getHotelDistanceFromSearch = async (
  {
    hotelDistanceFromSearchCriteria
  }: { hotelDistanceFromSearchCriteria: HotelDistanceFromSearchCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const hotelDistanceFromSearchPath = endpoints.HOTEL_DISTANCE_FROM_SEARCH.endpoint.replace(
      '{hotelId}',
      hotelDistanceFromSearchCriteria.hotelId
    );
    const hotelDistanceFromSearchEndPoint = getServiceEndpoint(
      hotelDistanceFromSearchPath,
      endpoints.HOTEL_DISTANCE_FROM_SEARCH
    );

    const fieldsToAdd = [
      { key: 'location', value: hotelDistanceFromSearchCriteria.location, required: true },
      {
        key: 'locationFormat',
        value: hotelDistanceFromSearchCriteria.locationFormat,
        required: true
      },
      { key: 'radius', value: hotelDistanceFromSearchCriteria.radius! || '0' },
      { key: 'radiusUnit', value: hotelDistanceFromSearchCriteria.radiusUnit }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to extract the single hotel availability: ${JSON.stringify(finalMap)}`);
    return await get(
      hotelDistanceFromSearchEndPoint,
      getHotelDistanceFromSearch,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
