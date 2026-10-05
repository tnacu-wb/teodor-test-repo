import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';

/**
 * This method is used to fetch the hotel availabilities by ids
 *
 * @param availabilityByIdsV3SearchCriteria
 * @param context
 * @throws Will throw an error if the hotel identifier is not provided
 * @throws Will throw an error if the room specifications are not provided
 * @throws Will throw an error if the arrival and departure dates are not provided, or if the arrival date is greater than the departure date
 * @returns A promise that resolves to the hotel availabilities data.
 */
export const getHotelAvailabilitiesByIdsV3 = async (
  { availabilityByIdsV3SearchCriteria }: { availabilityByIdsV3SearchCriteria: any },
  context: any
): Promise<any> => {
  try {
    // Extract isOta and spread the rest of the properties
    const { isOta, ...rest } = availabilityByIdsV3SearchCriteria;

    // Create a request body with isOTA (uppercase) instead of isOta
    const requestBody: any = {
      ...rest,
      isOTA: objectIsNullEmptyOrUndefined(isOta) ? false : isOta
    };

    return await post(
      endpoints.HOTEL_AVAILABILITY_BY_IDS_V3,
      getHotelAvailabilitiesByIdsV3,
      requestBody,
      context
    );
  } catch (error: Error | any) {
    handleError(error, availabilityByIdsV3SearchCriteria);
  }
};
