import { handleError } from '../../../exception/error-handler';
import { getURL } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * Service to cancel and replace an InnB PIBA card.
 * @param tetheredUserId
 * @param cardId
 * @param cancelAndReplaceInnBCardRequest
 * @param context
 */

export const cancelAndReplaceInnBPIBACard = async (
  {
    tetheredUserId,
    cardId,
    cancelAndReplaceInnBCardRequest
  }: {
    tetheredUserId: string;
    cardId: string;
    cancelAndReplaceInnBCardRequest: any;
  },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const cancelAndReplaceInnBPIBACardEndpoint =
      endpoints.CANCEL_AND_REPLACE_INNB_PIBA_CARD.endpoint
        .replace('{tetheredUserId}', tetheredUserId)
        .replace('{cardId}', cardId);

    const serviceEndpoint = {
      ...endpoints.CANCEL_AND_REPLACE_INNB_PIBA_CARD,
      endpoint: getURL(cancelAndReplaceInnBPIBACardEndpoint, finalMap)
    };

    return await post(
      serviceEndpoint,
      cancelAndReplaceInnBPIBACard,
      cancelAndReplaceInnBCardRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, { cancelAndReplaceInnBCardRequest });
  }
};
