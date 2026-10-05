import { CancelBookingRequest } from '../models/cancel-booking-request';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, getURL } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const cancelBooking = async (
  { cancelBookingRequest }: { cancelBookingRequest: CancelBookingRequest },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    const fieldsToAdd = [
      { key: 'channel', value: cancelBookingRequest.bookingChannel?.channel, required: true },
      { key: 'subchannel', value: cancelBookingRequest.bookingChannel?.subchannel, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    const endpointWithParams = getURL(endpoints.CANCEL_BOOKING.endpoint, finalMap);
    const serviceEndpoint = { ...endpoints.CANCEL_BOOKING, endpoint: endpointWithParams };
    return await post(serviceEndpoint, cancelBooking, cancelBookingRequest, context);
  } catch (error: Error | any) {
    handleError(error, cancelBookingRequest);
  }
};
