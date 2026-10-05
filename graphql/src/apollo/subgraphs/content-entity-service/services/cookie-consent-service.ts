import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

/**
 * This method is used to get cookie consent policies.
 *
 * @param country
 * @param language
 * @param brand
 * @param context contains the headers and client
 * @returns cookie consent policies information
 */
export const getCookieConsent = async (
  { country, language, brand }: { country: string; language: string; brand: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'country', value: country, required: true },
      { key: 'language', value: language, required: true },
      { key: 'brand', value: brand, required: true }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);

    return await get(endpoints.COOKIE_CONSENT, getCookieConsent, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};
