import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { addFieldsToMap } from '../../../utils/base-utils';

export const getNotifications = async (
  { tetheredUserId, scheme }: { tetheredUserId: string; scheme: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'tetheredUserId', value: tetheredUserId, required: false },
      { key: 'scheme', value: scheme, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.GET_NOTIFICATIONS, getNotifications, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

export const getNotificationsV2 = async (args: any, context: any): Promise<any> => {
  try {
    return await get(endpoints.GET_NOTIFICATIONS, getNotificationsV2, {}, context);
  } catch (error) {
    handleError(error, {});
  }
};

export const getAccountInfo = async (
  { tetheredUserId, scheme }: { tetheredUserId: string; scheme: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const fieldsToAdd = [
      { key: 'tetheredUserId', value: tetheredUserId, required: false },
      { key: 'scheme', value: scheme, required: true }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.GET_ACCOUNT_INFO, getAccountInfo, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};
