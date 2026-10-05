import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { addFieldIfNotUndefinedAndRequired } from '../../../utils/base-utils';

/**
 * This method is used to search resource id based on role id list.
 *
 * @param roleIdList
 * @param context contains header and client
 * @returns a list of resource ids.
 */
export const retrieveResourceId = async (
  { roleIdList }: { roleIdList: string },
  context: any
): Promise<any> => {
  let params: { [key: string]: any } = {};
  try {
    addFieldIfNotUndefinedAndRequired(roleIdList, 'roleIdList', params);

    return await get(endpoints.SEARCH_RESOURCE_ID, retrieveResourceId, params, context);
  } catch (error: Error | any) {
    handleError(error, params);
  }
};
