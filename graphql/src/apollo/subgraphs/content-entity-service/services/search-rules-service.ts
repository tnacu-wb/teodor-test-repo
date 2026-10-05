import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to fetch Max Rooms Limitation
 * @param channelId as channel id
 * @returns The response containing max rooms limitation
 */
export const getMaxRoomsLimitation = async (
  { channel }: { channel: string },
  context: any
): Promise<any> => {
  try {
    return await get(
      endpoints.MAX_ROOMS_LIMITATION,
      getMaxRoomsLimitation,
      { channelId: channel },
      context
    );
  } catch (error) {
    handleError(error, channel);
  }
};
