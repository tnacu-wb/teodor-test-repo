import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

/**
 * This method is used to send the confirmation email to a user.
 *
 * @param resendConfirmationRequest
 * @param context contains the headers and the client
 * @returns a string.
 */
export const resendConfirmationEmail = async (
  { resendConfirmationRequest }: { resendConfirmationRequest?: any },
  context: any
) => {
  try {
    return await post(
      endpoints.RESEND_CONFIRMATION_EMAIL,
      resendConfirmationEmail,
      resendConfirmationRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, resendConfirmationRequest);
  }
};
