import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to send a get request to retrieve cost centre details.
 *
 * @param tetheredUserGuid
 * @param context contains the headers and the client
 *
 * @returns a list containing the cost centre details.
 */
export const getCostCentreDetails = async (
  { tetheredUserGuid }: { tetheredUserGuid: string },
  context: any
): Promise<any> => {
  try {
    const getCostCentreDetailsEndpoint = endpoints.COST_CENTRE_DETAILS.endpoint.replace(
      '{tetheredUserGuid}',
      tetheredUserGuid
    );

    const serviceEndpoint = {
      ...endpoints.COST_CENTRE_DETAILS,
      endpoint: getCostCentreDetailsEndpoint
    };

    return await get(serviceEndpoint, getCostCentreDetails, {}, context);
  } catch (error) {
    handleError(error, tetheredUserGuid);
  }
};
