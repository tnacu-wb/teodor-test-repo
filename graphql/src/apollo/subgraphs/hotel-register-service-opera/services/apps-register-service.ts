import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';

export const appsRegistration = async (
  { appsRegistrationRequest }: { appsRegistrationRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.APPS_ACCOUNT_REGISTRATION,
      appsRegistration,
      appsRegistrationRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, appsRegistrationRequest);
  }
};
