import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to fetch the countries list
 * @param language The language of the countries
 * @param country country name(gb,de)
 * @param site site name(PI,BB)
 * @param headers The headers
 * @returns The response containing countries list
 */
export const getCountries = async (args: any, context: any): Promise<any> => {
  try {
    return await get(endpoints.COUNTRIES, getCountries, args, context);
  } catch (error) {
    handleError(error, args);
  }
};
