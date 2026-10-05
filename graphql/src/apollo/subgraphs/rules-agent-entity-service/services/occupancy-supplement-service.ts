import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to get the occupancy supplement rules
 * @param hotelIds The hotel ids
 * @param context The context object
 * @returns The response containing room occupancy limitations
 */
export const getOccupancySupplement = async (args: any, context: any): Promise<any> => {
  try {
    const resp = await post(
      endpoints.OCCUPANCY_SUPPLEMENT,
      getOccupancySupplement,
      { hotelIds: args.hotelIds },
      context
    );
    return resp.list ?? [];
  } catch (error) {
    handleError(error, args);
  }
};
