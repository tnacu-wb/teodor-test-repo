import { objectIsNullEmptyOrUndefined } from '../../../utils/base-utils';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { LoginCriteria } from '../models/login-criteria';
import { endpoints } from './base-service';

export const businessTetherLogin = async (
  { loginCriteria }: { loginCriteria: LoginCriteria },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.TETHER_LOGIN, businessTetherLogin, loginCriteria, context);
  } catch (error: Error | any) {
    throw handleError(error, loginCriteria);
  }
};
