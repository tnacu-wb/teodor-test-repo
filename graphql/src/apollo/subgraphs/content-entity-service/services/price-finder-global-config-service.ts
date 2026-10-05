import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getPriceFinderGlobalConfig = async (
  {
    channel,
    brand,
    country,
    language,
    path
  }: { channel: any; brand?: string; country?: string; language?: string; path?: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'channelId', value: channel, required: true },
      { key: 'brand', value: brand, required: false },
      { key: 'country', value: country, required: false },
      { key: 'language', value: language, required: false },
      { key: 'path', value: path, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    const response = await get(
      endpoints.PRICE_FINDER_GLOBAL_CONFIG,
      getPriceFinderGlobalConfig,
      finalMap,
      context
    );
    if (objectIsNullEmptyOrUndefined(response)) {
      return '{}';
    } else {
      return response;
    }
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
