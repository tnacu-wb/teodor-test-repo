import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import createLogger from '../../../log/logger';
import { basename } from 'path';
import {
  addFieldIfNotUndefined,
  amendHeaders,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';

const log = createLogger(basename(__filename));

export const getAccountList = async (
  { viewAll }: { viewAll: boolean },
  context: any
): Promise<any> => {
  try {
    context.headers['viewAll'] = viewAll;
    //viewAll will be sent as a header, not as a query param / body
    return await get(endpoints.GET_ACCOUNT_LIST, getAccountList, null, context);
  } catch (error) {
    handleError(error, { viewAll: viewAll });
  }
};
