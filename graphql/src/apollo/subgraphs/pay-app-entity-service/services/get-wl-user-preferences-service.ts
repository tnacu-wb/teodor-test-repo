import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { addFieldsToMap, objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';

export const getWorldlineUserPreferences = async (args: any, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    const fieldsToAdd = [
      { key: 'tetheredUserGuids', value: args.tetheredUserGuids, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(
      endpoints.GET_WORDLINE_USER_PREFERENCES,
      getWorldlineUserPreferences,
      finalMap,
      context
    );
  } catch (error: Error | any) {
    throw handleError(error, args.tetheredUserGuids);
  }
};
