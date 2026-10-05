import { AvailabilityByIdsV2SearchCriteria } from '../models/availability-by-ids-v2-search-criteria';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';

/**
 * This method is used to fetch the hotel availabilities by ids
 *
 * @param availabilityByIdsV2SearchCriteria
 * @param context
 * @throws Will throw an error if the hotel identifier is not provided
 * @throws Will throw an error if the room specifications are not provided
 * @throws Will throw an error if the arrival and departure dates are not provided, or if the arrival date is greater than the departure date
 * @returns A promise that resolves to the hotel availabilities data.
 */
export const getHotelAvailabilitiesByIdsV2 = async (
  {
    availabilityByIdsV2SearchCriteria
  }: { availabilityByIdsV2SearchCriteria: AvailabilityByIdsV2SearchCriteria },
  context: any
): Promise<any> => {
  try {
    let requestBody: any = (({ isOta, ...o }) => o)(availabilityByIdsV2SearchCriteria);

    requestBody.isOTA = objectIsNullEmptyOrUndefined(availabilityByIdsV2SearchCriteria.isOta)
      ? false
      : availabilityByIdsV2SearchCriteria.isOta;

    return await post(
      endpoints.HOTEL_AVAILABILITY_BY_IDS_V2,
      getHotelAvailabilitiesByIdsV2,
      requestBody,
      context
    );
  } catch (error: Error | any) {
    handleError(error, availabilityByIdsV2SearchCriteria);
  }
};
