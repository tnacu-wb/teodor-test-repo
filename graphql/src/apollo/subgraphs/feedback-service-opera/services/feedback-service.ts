import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to add feedback.
 *
 * @param feedback
 * @param context contains the headers and the client
 * @returns the response containing a reference ID.
 */
export const addFeedback = async ({ feedback }: { feedback: any }, context: any): Promise<any> => {
  try {
    return await post(endpoints.ADD_FEEDBACK, addFeedback, feedback, context);
  } catch (error: Error | any) {
    handleError(error, feedback);
  }
};
