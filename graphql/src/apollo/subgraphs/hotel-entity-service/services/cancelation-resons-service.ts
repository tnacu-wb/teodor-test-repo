import createLogger from '../../../log/logger';
import { basename } from 'path';
import { getServiceEndpoint } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

const log = createLogger(basename(__filename));

/**
 * This method is used to fetch the cancellation reasons
 *
 * @param hotelId
 * @param context
 * @throws Will throw an error if the hotel identifier is not provided
 * @returns A promise that resolves to the cancellation reasons data.
 */

export const getCancellationReasons = async (
  { hotelId }: { hotelId: string },
  context: any
): Promise<any> => {
  try {
    const cancelationReasonsPath = endpoints.CANCELLATION_REASONS.endpoint.replace(
      '{hotelId}',
      hotelId.toString()
    );

    const cancelationReasonsEndPoint = getServiceEndpoint(
      cancelationReasonsPath,
      endpoints.CANCELLATION_REASONS
    );
    return await get(cancelationReasonsEndPoint, getCancellationReasons, null, context);
  } catch (error: Error | any) {
    handleError(error, hotelId);
  }
};
