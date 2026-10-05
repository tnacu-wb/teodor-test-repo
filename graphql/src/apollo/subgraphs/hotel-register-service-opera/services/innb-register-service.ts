import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';

export const innBRegistrationStepOne = async (
  { innBRegistrationStepOneRequest }: { innBRegistrationStepOneRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.REGISTRATION_STEP_ONE,
      innBRegistrationStepOne,
      innBRegistrationStepOneRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, innBRegistrationStepOneRequest);
  }
};

export const innBRegistrationStepTwo = async (
  { innBRegistrationStepTwoRequest }: { innBRegistrationStepTwoRequest: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.REGISTRATION_STEP_TWO,
      innBRegistrationStepTwo,
      innBRegistrationStepTwoRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, innBRegistrationStepTwoRequest);
  }
};
