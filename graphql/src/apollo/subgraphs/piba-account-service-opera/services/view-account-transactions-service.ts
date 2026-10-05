import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const viewAccountTransactions = async (
  { payload }: { payload: any },
  context: any
): Promise<any> => {
  try {
    return await post(
      endpoints.VIEW_ACCOUNT_TRANSACTIONS,
      viewAccountTransactions,
      payload,
      context
    );
  } catch (error) {
    handleError(error, payload);
  }
};
