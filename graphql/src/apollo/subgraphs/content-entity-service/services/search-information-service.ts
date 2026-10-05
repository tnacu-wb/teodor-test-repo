import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getSearchInformation = async (
  { language, country }: { language: string; country: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'language', value: language, required: true },
      { key: 'country', value: country, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.SEARCH_INFORMATION, getSearchInformation, finalMap, context);
  } catch (error: Error | any) {
    throw handleError(error, finalMap);
  }
};
