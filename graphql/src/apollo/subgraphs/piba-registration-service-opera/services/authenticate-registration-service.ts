import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

type AuthenticationAnswers = {
  question: string;
  answer: string;
};

export type AuthenticateRegistrationRequest = {
  registrationCode: string;
  authenticationAnswers?: AuthenticationAnswers[];
};

export const authenticateRegistration = async (
  {
    authenticateRegistrationRequest
  }: { authenticateRegistrationRequest: AuthenticateRegistrationRequest },
  context: any
) => {
  try {
    return await post(
      endpoints.AUTHENTICATE_REGISTRATION,
      authenticateRegistration,
      authenticateRegistrationRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, authenticateRegistrationRequest);
  }
};
