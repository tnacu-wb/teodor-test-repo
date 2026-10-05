import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { addFieldsToMap, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { handleError } from '../../../exception/error-handler';
import { BookingHistoryRequest } from '../models/booking-history-request';

/**
 * This method is used to fetch the booking history for a user.
 *
 * @param bookingHistoryRequest
 * @param context contains the headers and the client
 * @returns The response containing the booking history.
 */
export const retrieveBookingHistory = async (
  { bookingHistoryRequest }: { bookingHistoryRequest: BookingHistoryRequest },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [
      { key: 'sortOrder', value: bookingHistoryRequest.sortOrder, required: false },
      { key: 'business', value: bookingHistoryRequest.business, required: false },
      { key: 'typeOfBooking', value: bookingHistoryRequest.typeOfBooking, required: false },
      { key: 'filterType', value: bookingHistoryRequest.filterType, required: false },
      { key: 'filterValue', value: bookingHistoryRequest.filterValue, required: false },
      { key: 'continuationToken', value: bookingHistoryRequest.continuationToken, required: false },
      { key: 'pageSize', value: bookingHistoryRequest.pageSize, required: false },
      { key: 'pageIndex', value: bookingHistoryRequest.pageIndex, required: false },
      {
        key: 'channel',
        value: bookingHistoryRequest.bookingChannel?.channel,
        required: !objectIsNullEmptyOrUndefined(bookingHistoryRequest.bookingChannel)
      },
      {
        key: 'subchannel',
        value: bookingHistoryRequest.bookingChannel?.subchannel,
        required: !objectIsNullEmptyOrUndefined(bookingHistoryRequest.bookingChannel)
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await post(endpoints.BOOKING_HISTORY, retrieveBookingHistory, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, bookingHistoryRequest);
  }
};
