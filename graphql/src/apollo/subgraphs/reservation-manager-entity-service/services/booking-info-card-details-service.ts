import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { addFieldsToMap } from '../../../utils/base-utils';
import { handleError } from '../../../exception/error-handler';
import { BookingInfoCardRequest } from '../models/booking-info-card-request';

/**
 * This method is used to fetch the booking info card details for a user.
 *
 * @param bookingInfoCardRequest
 * @param context contains the headers and the client
 * @returns The response containing the booking info card.
 */
export const retrieveBookingInfoCardDetails = async (
  { bookingInfoCardRequest }: { bookingInfoCardRequest?: BookingInfoCardRequest },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [
      { key: 'country', value: bookingInfoCardRequest?.country, required: false },
      { key: 'language', value: bookingInfoCardRequest?.language, required: false },
      { key: 'sourceSystem', value: bookingInfoCardRequest?.sourceSystem, required: false },
      { key: 'token', value: bookingInfoCardRequest?.token, required: false },
      { key: 'bookingReference', value: bookingInfoCardRequest?.bookingReference, required: true },
      { key: 'hotelId', value: bookingInfoCardRequest?.hotelId, required: true },
      { key: 'arrival', value: bookingInfoCardRequest?.arrival, required: true },
      { key: 'surname', value: bookingInfoCardRequest?.surname, required: true },
      { key: 'channel', value: bookingInfoCardRequest?.bookingChannel.channel, required: true },
      {
        key: 'subchannel',
        value: bookingInfoCardRequest?.bookingChannel.subchannel,
        required: true
      }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(
      endpoints.BOOKING_INFO_CARD_DETAILS,
      retrieveBookingInfoCardDetails,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, bookingInfoCardRequest);
  }
};
