import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const validateResetKey = async (
  { validateResetKeyRequest }: { validateResetKeyRequest?: any },
  context: any
) => {
  try {
    return await post(
      endpoints.VALIDATE_RESET_KEY,
      validateResetKey,
      validateResetKeyRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, validateResetKeyRequest);
  }
};
