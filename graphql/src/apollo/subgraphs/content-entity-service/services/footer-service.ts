import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to fetch the footer
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param site site name(PI,BB)
 * @returns The response containing countries list
 */
export const getFooter = async (args: any, context: any): Promise<any> => {
  try {
    return await get(endpoints.FOOTER, getFooter, args, context);
  } catch (error) {
    handleError(error, args);
  }
};
