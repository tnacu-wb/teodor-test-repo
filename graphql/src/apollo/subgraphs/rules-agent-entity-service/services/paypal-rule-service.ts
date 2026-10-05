import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';

/**
 * This method is used to get the paypal rule for the given channel, country and hotel id
 * @param channel The channel id
 * @param country The country code
 * @param hotelId The hotel id
 * @param context The context object
 * @returns The response containing the paypal rule
 */
export const getPaypalRule = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'channelId', value: args.channel, required: true },
      { key: 'country', value: args.country, required: true },
      { key: 'hotelId', value: args.hotelId, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);
    return await get(endpoints.PAYPAL_RULE, getPaypalRule, finalMap, context);
  } catch (error) {
    handleError(error, args);
  }
};
