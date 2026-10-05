import { handleError } from '../../../exception/error-handler';
import { addFieldIfNotUndefinedAndRequired } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getCardManagementLabels = async (
  { language }: { language: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    addFieldIfNotUndefinedAndRequired(language, 'language', finalMap);

    return await get(
      endpoints.GET_CARD_MANAGEMENT_LABELS,
      getCardManagementLabels,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
