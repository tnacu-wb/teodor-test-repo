import { endpoints } from './base-service';
import { addFieldIfNotUndefinedAndRequired } from '../../../utils/base-utils';
import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';

/**
 * This method is used to fetch the anonymous newsletter preferences.
 *
 * @param email
 * @param brandCode
 * @param countryOfResidence
 * @param language
 * @param context contains the headers and the client
 * @returns The response containing the anonymous newsletter preferences.
 */
export const retrieveAnonymousNewsletterPreferences = async (
  {
    email,
    brandCode,
    countryOfResidence,
    language
  }: { email: string; brandCode: string; countryOfResidence: string; language: string },
  context: any
): Promise<any> => {
  try {
    const anonymousNewsletterPreferencesEndpoint =
      endpoints.ANONYMOUS_NEWSLETTER_PREFERENCES.endpoint.replace('{email}', email);
    const serviceEndpoint = {
      ...endpoints.ANONYMOUS_NEWSLETTER_PREFERENCES,
      endpoint: anonymousNewsletterPreferencesEndpoint
    };

    let finalMap: { [key: string]: any } = {};

    addFieldIfNotUndefinedAndRequired(brandCode, 'brandCode', finalMap);
    addFieldIfNotUndefinedAndRequired(countryOfResidence ?? 'gb', 'countryOfResidence', finalMap);
    addFieldIfNotUndefinedAndRequired(language ?? 'en', 'language', finalMap);

    return await get(serviceEndpoint, retrieveAnonymousNewsletterPreferences, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, brandCode);
  }
};
