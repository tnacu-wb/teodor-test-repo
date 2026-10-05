import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to initiate a SCA card storage session.
 *
 * @param initiateAuthorizeScaRequest
 * @param context contains the headers and the client
 * @returns the response containing the payment required details.
 */
export const initiateAuthorizeCard = async (
  { initiateAuthorizeScaRequest }: { initiateAuthorizeScaRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.AUTHORIZE_CARD,
      initiateAuthorizeCard,
      initiateAuthorizeScaRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, initiateAuthorizeScaRequest);
  }
};
