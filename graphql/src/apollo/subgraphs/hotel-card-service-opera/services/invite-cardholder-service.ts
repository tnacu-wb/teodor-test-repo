import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { getURL } from '../../../utils/base-utils';

/**
 * This method is used to send a post request to invite someone to be a registered cardholder (resend code).
 *
 * @param tetheredUserGuid
 * @param cardId
 * @param inviteCardHolderRequest
 * @param context contains the headers and the client
 *
 * @returns a string containing the response of the invitation.
 */
export const inviteCardHolder = async (
  {
    tetheredUserGuid,
    cardId,
    inviteCardHolderRequest
  }: {
    tetheredUserGuid: string;
    cardId: string;
    inviteCardHolderRequest: any;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const inviteCardHolderEndpoint = endpoints.INVITE_CARDHOLDER.endpoint
      .replace('{tetheredUserGuid}', tetheredUserGuid)
      .replace('{cardId}', cardId);

    const serviceEndpoint = {
      ...endpoints.INVITE_CARDHOLDER,
      endpoint: getURL(inviteCardHolderEndpoint, finalMap)
    };

    return await post(serviceEndpoint, inviteCardHolder, inviteCardHolderRequest, context);
  } catch (error) {
    handleError(error, inviteCardHolderRequest);
  }
};
