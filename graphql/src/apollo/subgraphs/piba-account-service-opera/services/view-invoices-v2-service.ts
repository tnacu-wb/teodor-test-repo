import { endpoints } from './base-service';
import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';

export const viewCustomerInvoicesV2 = async (
  { payload }: { payload: any },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.VIEW_INVOICES_V2, viewCustomerInvoicesV2, payload, context);
  } catch (error) {
    handleError(error, payload);
  }
};
