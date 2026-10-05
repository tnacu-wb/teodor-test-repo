import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getBookingFlowInformation = async (
  {
    language,
    country,
    bookingFlowId,
    hotelId
  }: { language: string; country: string; bookingFlowId: string; hotelId: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'language', value: language, required: true },
      { key: 'country', value: country, required: true },
      { key: 'bookingFlowId', value: bookingFlowId, required: true },
      { key: 'hotelId', value: hotelId, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(
      endpoints.BOOKING_FLOW_INFORMATION,
      getBookingFlowInformation,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
