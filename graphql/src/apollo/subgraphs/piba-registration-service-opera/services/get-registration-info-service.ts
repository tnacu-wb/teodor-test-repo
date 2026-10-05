import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { replaceServiceEndpoint } from '../../../utils/base-utils';
import { mapRegistrationRole } from '../utils/map-registration-role';

export type GetRegistrationInfoArgs = {
  registrationCode: string;
};

export const getRegistrationInfo = async (
  { registrationCode }: GetRegistrationInfoArgs,
  context: any
): Promise<any> => {
  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.GET_REGISTRATION_INFO,
      '{registrationCode}',
      registrationCode
    );

    const response = await get(
      serviceEndpoint,
      getRegistrationInfo,
      null, // registrationCode will be sent as a path param
      context
    );

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
    handleError(error, { registrationCode: registrationCode });
  }
};
