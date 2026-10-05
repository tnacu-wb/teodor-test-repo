import { CreateAccountRequest } from '../models/create-account-request';
import { handleError } from '../../../exception/error-handler';
import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const createAccount = async (
  { createAccountRequest }: { createAccountRequest: CreateAccountRequest },
  context: any
): Promise<any> => {
  try {
    return await post(endpoints.CREATE_ACCOUNT, createAccount, createAccountRequest, context);
  } catch (error: Error | any) {
    handleError(error, createAccountRequest);
  }
};
