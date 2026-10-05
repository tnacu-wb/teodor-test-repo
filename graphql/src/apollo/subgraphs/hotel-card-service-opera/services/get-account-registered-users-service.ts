import { handleError } from '../../../exception/error-handler';
import {
  addFieldIfNotUndefinedAndRequired,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the account registered users.
 *
 * @param tetheredUserId
 * @param countryCode
 * @param context contains the headers and the client
 * @returns The response containing the account registered users list.
 */
export const retrieveAccountRegisteredUsers = async (
  { tetheredUserId, countryCode }: { tetheredUserId: string; countryCode: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const accountRegisteredUsersEndpoint = endpoints.GET_ACCOUNT_REGISTERED_USERS.endpoint.replace(
      '{tetheredUserId}',
      tetheredUserId
    );
    const serviceEndpoint = {
      ...endpoints.GET_ACCOUNT_REGISTERED_USERS,
      endpoint: accountRegisteredUsersEndpoint
    };
    addFieldIfNotUndefinedAndRequired(countryCode, 'countryCode', finalMap);

    return await get(serviceEndpoint, retrieveAccountRegisteredUsers, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
