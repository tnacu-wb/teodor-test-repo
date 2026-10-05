import { handleError } from '../../../exception/error-handler';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to initiate card storage session.
 *
 * @param initiateSaveCardRequest
 * @param context contains the headers and the client
 * @returns the response containing the payment required details.
 */
export const initiateSaveCard = async (
  { initiateSaveCardRequest }: { initiateSaveCardRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.SAVE_CARD, initiateSaveCard, initiateSaveCardRequest, context);
  } catch (error: Error | any) {
    handleError(error, initiateSaveCardRequest);
  }
};
