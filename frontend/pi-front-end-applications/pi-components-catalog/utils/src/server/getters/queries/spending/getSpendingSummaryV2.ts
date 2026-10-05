import { getAccountBalanceSummaryV2, Scheme } from '@whitbread-eos/api';

import { executeGraphQLQuery } from '../../../index';

export const getSpendingSummaryV2 = async (
  token: string,
  scheme: Scheme,
  tetheredUserGuid: string
) => {
  const revalidateCache = false;
  const returnErrors = true;
  if (!token) {
    throw new Error('Authentication token is required');
  }

  try {
    const response = await executeGraphQLQuery(
      getAccountBalanceSummaryV2(),
      {
        scheme,
        tetheredUserGuid,
      },
      (result: unknown) => result,
      token,
      revalidateCache,
      returnErrors
    );
    return response;
  } catch (error) {
    return null;
  }
};
