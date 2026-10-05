import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';
import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';

export const updateAppCompanyDetails = async (args: any, context: any): Promise<any> => {
  try {
    if (!objectIsNullEmptyOrUndefined(context.headers?.['X-Forwarded-For'])) {
      context.headers['X-Forwarded-For'] = context.headers?.['X-Forwarded-For'];
    }

    return await post(
      endpoints.UPDATE_APP_COMPANY_DETAILS,
      updateAppCompanyDetails,
      args.updateAppCompanyDetailsCriteria,
      context
    );
  } catch (error: Error | any) {
    throw handleError(error, args.updateAppCompanyDetailsCriteria);
  }
};
