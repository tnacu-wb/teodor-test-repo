import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getRoomClassConfig = async (
  {
    channel,
    brand,
    country,
    language
  }: { channel: any; brand?: string; country?: string; language?: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'channelId', value: channel, required: true },
      { key: 'brand', value: brand, required: false },
      { key: 'country', value: country, required: false },
      { key: 'language', value: language, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    const response = await get(endpoints.ROOM_CLASS_CONFIG, getRoomClassConfig, finalMap, context);
    if (objectIsNullEmptyOrUndefined(response)) {
      return '{}';
    } else {
      return response;
    }
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
