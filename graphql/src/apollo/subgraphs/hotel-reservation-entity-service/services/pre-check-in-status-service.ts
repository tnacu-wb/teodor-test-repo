import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to save Pre-CheckIn status of a Reservation
 *
 * @param preCheckInCriteria
 * @param context contains the headers and the client
 * @returns a response containing the status and a message.
 */
export const savePreCheckInStatus = async (
  { preCheckInCriteria }: { preCheckInCriteria: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.PRE_CHECK_IN_STATUS,
      savePreCheckInStatus,
      preCheckInCriteria,
      context
    );
  } catch (error: Error | any) {
    handleError(error, preCheckInCriteria);
  }
};
