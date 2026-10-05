import { getAccountPaymentsQuery } from '@whitbread-eos/api';

import { executeGraphQLQuery } from '../../../';

const getAccountPayments = async (
  token: string,
  accountId: string,
  page: number,
  size: number,
  tetheredUserGuid: string,
  nonInvoiceOnly = false
) => {
  return await executeGraphQLQuery(
    getAccountPaymentsQuery(),
    {
      paymentInfoCriteria: {
        accountId: accountId,
        tetheredUserGuid: tetheredUserGuid,
        nonInvoiceOnly: nonInvoiceOnly,
        page: page,
        size: size,
      },
    },
    (result: any) => result.data.getPaymentInfo,
    token,
    true,
    false
  );
};

export default getAccountPayments;
