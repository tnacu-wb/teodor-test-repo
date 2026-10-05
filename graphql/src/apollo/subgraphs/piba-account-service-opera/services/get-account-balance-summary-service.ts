import { handleError } from '../../../exception/error-handler';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';

export const getAccountBalanceSummary = async (
  { viewAll }: { viewAll: boolean },
  context: any
): Promise<any> => {
  try {
    context.headers['viewAll'] = viewAll;
    return await get(
      endpoints.GET_ACCOUNT_BALANCE_SUMMARY,
      getAccountBalanceSummary,
      null, //viewAll will be sent as a header, not as a query param / body
      context
    );
  } catch (error: Error | any) {
    handleError(error, { viewAll: viewAll });
  }
};

export const getAccountBalanceSummaryV2 = async (
  { tetheredUserGuid, scheme }: { tetheredUserGuid: string; scheme: string },
  context: any
): Promise<any> => {
  try {
    let endpoint = endpoints.GET_ACCOUNT_BALANCE_SUMMARY_V2.endpoint;
    endpoint = endpoint.replace('{tetheredUserGuid}', tetheredUserGuid);
    endpoint = endpoint.replace('{scheme}', scheme);

    const serviceEndpoint = {
      ...endpoints.GET_ACCOUNT_BALANCE_SUMMARY_V2,
      endpoint: endpoint
    };
    return await get(serviceEndpoint, getAccountBalanceSummaryV2, {}, context);
  } catch (error: Error | any) {
    handleError(error, {});
  }
};
