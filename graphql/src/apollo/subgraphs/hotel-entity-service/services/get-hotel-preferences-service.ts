import {
  addFieldsToMap,
  getServiceEndpoint,
  objectIsNullEmptyOrUndefined,
  validateDateRange
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the hotel preferences
 *
 * @param hotelId
 * @param preferenceGroupsCodes
 * @param language
 * @param context
 * @throws Will throw an error if the hotel identifier is not provided or if there is an error in
 * fetching the preferences.
 * @returns A promise that resolves to the hotel preferences data.
 */
export const getHotelPreferences = async (
  {
    hotelId,
    preferenceGroupsCodes,
    language
  }: { hotelId: string; preferenceGroupsCodes: string; language?: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const hotelPreferencesPath = endpoints.HOTEL_PREFERENCES.endpoint.replace(
      '{hotelId}',
      hotelId.toString()
    );

    const hotelPreferencesEndPoint = getServiceEndpoint(
      hotelPreferencesPath,
      endpoints.HOTEL_PREFERENCES
    );
    const fieldsToAdd = [
      { key: 'preferenceGroupsCodes', value: preferenceGroupsCodes, required: true },
      { key: 'language', value: language, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to extract the hotel preferences: ${JSON.stringify(finalMap)}`);
    return await get(hotelPreferencesEndPoint, getHotelPreferences, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
