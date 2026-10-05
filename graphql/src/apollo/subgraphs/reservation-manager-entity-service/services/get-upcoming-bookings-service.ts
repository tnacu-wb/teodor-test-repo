import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { addFieldsToMap } from '../../../utils/base-utils';

export const getUpcomingBookings = async (args: any, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldToAdd = [
      { key: 'language', value: args.upcomingBookingsRequest.language, required: true },
      { key: 'country', value: args.upcomingBookingsRequest.country, required: true },
      { key: 'channel', value: args.upcomingBookingsRequest.channel, required: false },
      { key: 'subchannel', value: args.upcomingBookingsRequest.subchannel, required: false }
    ];

    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.GET_UPCOMING_BOOKINGS, getUpcomingBookings, finalMap, context);
  } catch (error: Error | any) {
    throw handleError(error, args);
  }
};
