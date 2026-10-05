import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const dlpInformation = async (
  { language, country, dlpPath }: { language: string; country: string; dlpPath: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'language', value: language, required: true },
      { key: 'country', value: country, required: true },
      { key: 'dlpPath', value: dlpPath, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.DLP_INFORMATION, dlpInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
