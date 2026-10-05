import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { mapRegistrationRole } from '../utils/map-registration-role';

export const submitRegistration = async (
  { registrationSubmitRequest }: { registrationSubmitRequest?: any },
  context: any
) => {
  try {
    const response = await post(
      endpoints.SUBMIT_REGISTRATION,
      submitRegistration,
      registrationSubmitRequest,
      context
    );
    console.log('test' + response);
    // Map the registrationRole in the response
    if (response?.registrationCodeInfo?.registrationRole) {
      const mappedRole = mapRegistrationRole(response.registrationCodeInfo.registrationRole);
      if (!mappedRole) {
        throw new Error(
          `Invalid registrationRole: ${response.registrationCodeInfo.registrationRole}`
        );
      }
      response.registrationCodeInfo.registrationRole = mappedRole;
    }
    return response;
  } catch (error: Error | any) {
    handleError(error, registrationSubmitRequest);
  }
};
