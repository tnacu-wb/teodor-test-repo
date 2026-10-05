import { endpoints } from './base-service';
import { AvailabilityByIdsSearchCriteria } from '../models/availability-by-ids-search-criteria';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import { addFieldsToMap, joinCriteria, validateDateRange } from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the hotel availabilities by ids
 *
 * @param availabilityByIdsSearchCriteria
 * @param context
 * @throws Will throw an error if the arrival and departure dates are not provided, or if the arrival date is greater than the departure date
 * @returns A promise that resolves to the hotel availabilities data.
 */
export const getHotelAvailabilitiesByIds = async (
  {
    availabilityByIdsSearchCriteria
  }: { availabilityByIdsSearchCriteria: AvailabilityByIdsSearchCriteria },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const comma = ',';
    const fieldsToAdd = [
      {
        key: 'hotelIds',
        value: joinCriteria(availabilityByIdsSearchCriteria.hotels, 'identifier', comma),
        required: true
      },
      { key: 'arrivalDate', value: availabilityByIdsSearchCriteria.arrival, required: true },
      { key: 'departureDate', value: availabilityByIdsSearchCriteria.departure, required: true },
      {
        key: 'roomTypes',
        value: joinCriteria(availabilityByIdsSearchCriteria.rooms, 'roomType', comma),
        required: true
      },
      {
        key: 'adultsNumber',
        value: joinCriteria(availabilityByIdsSearchCriteria.rooms, 'adultsNumber', comma),
        required: true
      },
      {
        key: 'childrenNumber',
        value: joinCriteria(availabilityByIdsSearchCriteria.rooms, 'childrenNumber', comma),
        required: true
      },
      {
        key: 'cotsRequired',
        value: joinCriteria(availabilityByIdsSearchCriteria.rooms, 'cotRequired', comma)!,
        required: false
      },
      {
        key: 'ratePlanCodes',
        value: joinCriteria(availabilityByIdsSearchCriteria.ratePlanCodes!, '', comma)!,
        required: false
      },
      {
        key: 'channel',
        value: availabilityByIdsSearchCriteria.bookingChannel?.channel,
        required: false
      },
      {
        key: 'subchannel',
        value: availabilityByIdsSearchCriteria.bookingChannel?.subchannel,
        required: false
      },
      {
        key: 'language',
        value: availabilityByIdsSearchCriteria.bookingChannel?.language,
        required: false
      },
      {
        key: 'vatNotRequired',
        value: availabilityByIdsSearchCriteria.vatNotRequired ?? false,
        required: false
      },
      { key: 'isOTA', value: availabilityByIdsSearchCriteria.isOta ?? false, required: false },
      {
        key: 'pmsRoomTypes',
        value: joinCriteria(availabilityByIdsSearchCriteria.rooms, 'pmsRoomType', comma)!,
        required: false
      },
      {
        key: 'globalCompanyId',
        value: availabilityByIdsSearchCriteria.negotiatedRates?.globalCompanyId,
        required: false
      },
      {
        key: 'negotiatedRateDisplaySets',
        value: availabilityByIdsSearchCriteria.negotiatedRates?.rateDisplaySets?.join(comma) || '',
        required: false
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    log.info(`Final map to extract the hotel availability: ${JSON.stringify(finalMap)}`);
    return await get(
      endpoints.HOTEL_AVAILABILITY_BY_IDS,
      getHotelAvailabilitiesByIds,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
