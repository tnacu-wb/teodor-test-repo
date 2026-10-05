import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import {
  addFieldIfNotUndefined,
  getURL,
  objectIsNullEmptyOrUndefined
} from '../../../utils/base-utils';

const LANGUAGE = 'language';

export const forgotPassword = async (
  {
    language,
    innBusiness,
    forgottenPasswordRequest
  }: { language?: string; innBusiness?: boolean; forgottenPasswordRequest: any },
  context: any
): Promise<any> => {
  try {
    let finalMap: { [key: string]: any } = {};
    addFieldIfNotUndefined(innBusiness, 'innBusiness', finalMap);

    const serviceEndpoint = {
      ...endpoints.FORGOT_PASSWORD,
      endpoint: getURL(endpoints.FORGOT_PASSWORD.endpoint, finalMap)
    };

    if (!objectIsNullEmptyOrUndefined(language)) {
      context.headers[LANGUAGE] = language;
    }

    return await post(serviceEndpoint, forgotPassword, forgottenPasswordRequest, context);
  } catch (error) {
    handleError(error, forgottenPasswordRequest);
  }
};
