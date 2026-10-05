import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';

export const retrieveChangesLog = async (
  {
    hotelId,
    reservationId,
    limit,
    offset
  }: {
    hotelId: string;
    reservationId: string;
    limit?: number;
    offset?: number;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'hotelId', value: hotelId, required: true },
      { key: 'reservationId', value: reservationId, required: true },
      { key: 'limit', value: limit, required: false },
      { key: 'offset', value: offset, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.RETRIEVE_CHANGES_LOG, retrieveChangesLog, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
