import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const resetMemorableWord = async (
  { resetMemorableWordRequest }: { resetMemorableWordRequest?: any },
  context: any
) => {
  try {
    return await post(
      endpoints.RESET_MEMORABLE_WORD,
      resetMemorableWord,
      resetMemorableWordRequest,
      context
    );
  } catch (error: Error | any) {
    handleError(error, resetMemorableWordRequest);
  }
};
