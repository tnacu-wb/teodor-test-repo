import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { addFieldsToMap } from '../../../utils/base-utils';

export const homepageAppsContent = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'channel', value: args.channel, required: true },
      { key: 'subchannel', value: args.subchannel, required: true },
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    const response = await get(
      endpoints.HOMEPAGE_APPS_CONTENT,
      homepageAppsContent,
      finalMap,
      context
    );

    if (response.logo && typeof response.logo === 'string') {
      response.logo = `{imagePath=${response.logo}}`;
    } else if (response.logo && response.logo.imagePath) {
      response.logo = `{imagePath=${response.logo.imagePath}}`;
    }

    return response;
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
