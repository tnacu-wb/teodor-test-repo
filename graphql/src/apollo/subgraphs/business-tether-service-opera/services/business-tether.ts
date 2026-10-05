import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';

export const businessTether = async (args: any, context: any): Promise<any> => {
  try {
    return await post(endpoints.BUSINESS_TETHER, businessTether, args.tetherLinkRequest, context);
  } catch (error: Error | any) {
    throw handleError(error, args.tetherLinkRequest);
  }
};
