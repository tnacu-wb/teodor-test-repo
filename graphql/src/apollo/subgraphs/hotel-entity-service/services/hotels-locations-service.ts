import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import { addFieldsToMap } from '../../../utils/base-utils';
import { HotelsSearchCriteria } from '../models/hotels-search-criteria';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the hotel distance from search
 *
 * @param hotelSearchCriteria
 * @param context
 */

export const getHotelsLocations = async (
  { hotelsSearchCriteria }: { hotelsSearchCriteria: HotelsSearchCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'location', value: hotelsSearchCriteria.location, required: true },
      { key: 'locationFormat', value: hotelsSearchCriteria.locationFormat, required: true },
      { key: 'radius', value: hotelsSearchCriteria.radius!, required: true },
      { key: 'radiusUnit', value: hotelsSearchCriteria.radiusUnit, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to extract the single hotel availability: ${JSON.stringify(finalMap)}`);
    return await get(endpoints.HOTELS_LOCATIONS, getHotelsLocations, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
