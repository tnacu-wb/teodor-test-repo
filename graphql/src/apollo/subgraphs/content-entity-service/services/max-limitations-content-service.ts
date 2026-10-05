import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';

/**
 * This method is used to get max nights limitations
 * @param channel The channel id
 * @param context The context object
 * @returns The response containing max nights limitations
 */
export const getMaxNightsLimitation = async (
  { channel }: { channel: string },
  context: any
): Promise<any> => {
  try {
    return await get(
      endpoints.MAX_NIGHTS_LIMITATIONS,
      getMaxNightsLimitation,
      { channelId: channel },
      context
    );
  } catch (error) {
    handleError(error, channel);
  }
};

/**
 * This method is used to get maximum arrival date limitations
 * @param channel The channel id
 * @param context The context object
 * @returns The response containing maximum arrival date limitations
 */
export const getMaxArrivalDateLimitation = async (
  { channel }: { channel: string },
  context: any
): Promise<any> => {
  try {
    return await get(
      endpoints.MAX_ARRIVAL_DATE_LIMITATIONS,
      getMaxArrivalDateLimitation,
      { channelId: channel },
      context
    );
  } catch (error) {
    handleError(error, channel);
  }
};

/**
 * This method is used to get maximum arrival date limitations
 * @param channel The channel id
 * @param brand The brand
 * @param context The context object
 * @returns The response containing maximum arrival date limitations
 */
export const getRoomOccupancyLimitation = async (
  { channel, brand }: { channel: string; brand: string },
  context: any
): Promise<any> => {
  try {
    return await get(
      endpoints.MAX_ROOM_OCCUPANCY,
      getRoomOccupancyLimitation,
      { channelId: channel, brand: brand },
      context
    );
  } catch (error) {
    handleError(error, { channel, brand });
  }
};
