import createLogger from '../../../log/logger';
import { basename } from 'path';
import { handleError } from '../../../exception/error-handler';
import { HotelDistanceFromSearchCriteria } from '../models/hotel-distance-from-search-criteria';
import {
  addFieldsToMap,
  getServiceEndpoint,
  objectIsNullEmptyOrUndefined,
  validateDateRange,
  validateInteger
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

const log = createLogger(basename(__filename));

/**
 * This service is used to fetch the hotel inventory based on the provided criteria.
 *
 * @param hotelId - The identifier of the hotel.
 * @param dateRangeStart - The start date of the inventory range.
 * @param dateRangeEnd - The end date of the inventory range.
 * @returns A promise that resolves to the hotel inventory data.
 * @throws Will throw an error if the hotel identifier is not provided or if there is an error in fetching the inventory.
 */

export const getHotelInventory = async (
  {
    hotelId,
    dateRangeStart,
    dateRangeEnd
  }: { hotelId: string; dateRangeStart: string; dateRangeEnd: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const hotelInventoryPath = endpoints.HOTEL_INVENTORY.endpoint.replace(
      '{hotelId}',
      hotelId.toString()
    );

    const hotelInventoryEndPoint = getServiceEndpoint(
      hotelInventoryPath,
      endpoints.HOTEL_INVENTORY
    );

    const fieldsToAdd = [
      { key: 'dateRangeStart', value: dateRangeStart, required: true },
      { key: 'dateRangeEnd', value: dateRangeEnd, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to extract the hotel inventory: ${JSON.stringify(finalMap)}`);
    return await get(hotelInventoryEndPoint, getHotelInventory, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
