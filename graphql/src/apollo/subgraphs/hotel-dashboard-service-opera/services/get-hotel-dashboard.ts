import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { addFieldsToMap } from '../../../utils/base-utils';

export const getHotelDashboard = async (args: any, context: any): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};

    const fieldsToAdd = [
      { key: 'business', value: args.dashboardRequest.business, required: false },
      { key: 'customerId', value: args.customerId, required: false },
      { key: 'hasRecentSearches', value: args.hasRecentSearches, required: false },
      { key: 'surname', value: args.dashboardRequest.surname, required: false },
      {
        key: 'confirmationNumber',
        value: args.dashboardRequest.confirmationNumber,
        required: false
      },
      { key: 'language', value: args.dashboardRequest.language, required: false },
      { key: 'arrivalDate', value: args.dashboardRequest.arrivalDate, required: false },
      { key: 'employeeId', value: args.dashboardRequest.employeeId, required: false }
    ];

    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.GET_HOTEL_DASHBOARD, getHotelDashboard, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, { customerId: args.customerId });
  }
};
