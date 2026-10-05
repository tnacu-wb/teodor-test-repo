import { getAccountPaymentsQuery } from '@whitbread-eos/api';

import { executeGraphQLQuery } from '../../../';
import getAccountPayments from './getAccountPayments';

jest.mock('../../../', () => ({
  executeGraphQLQuery: jest.fn(),
}));
jest.mock('@whitbread-eos/api', () => ({
  getAccountPaymentsQuery: jest.fn(() => 'MOCK_QUERY'),
}));

describe('getAccountPayments', () => {
  const token = 'test-token';
  const accountId = 'account-123';
  const page = 1;
  const size = 10;
  const tetheredUserGuid = 'user-guid';
  const nonInvoiceOnly = true;

  beforeEach(() => {
    (executeGraphQLQuery as jest.Mock).mockClear();
  });

  it('should call executeGraphQLQuery with correct arguments', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('mocked-result');

    const result = await getAccountPayments(
      token,
      accountId,
      page,
      size,
      tetheredUserGuid,
      nonInvoiceOnly
    );

    expect(getAccountPaymentsQuery).toHaveBeenCalled();
    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      {
        paymentInfoCriteria: {
          accountId,
          tetheredUserGuid,
          nonInvoiceOnly,
          page,
          size,
        },
      },
      expect.any(Function),
      token,
      true,
      false
    );
    expect(result).toBe('mocked-result');
  });

  it('should default nonInvoiceOnly to false if not provided', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue('mocked-result-2');

    await getAccountPayments(
      token,
      accountId,
      page,
      size,
      tetheredUserGuid
      // nonInvoiceOnly omitted
    );

    expect(executeGraphQLQuery).toHaveBeenCalledWith(
      'MOCK_QUERY',
      {
        paymentInfoCriteria: {
          accountId,
          tetheredUserGuid,
          nonInvoiceOnly: false,
          page,
          size,
        },
      },
      expect.any(Function),
      token,
      true,
      false
    );
  });

  it('should pass through the result from executeGraphQLQuery', async () => {
    (executeGraphQLQuery as jest.Mock).mockResolvedValue({ data: 'some-data' });

    const result = await getAccountPayments(token, accountId, page, size, tetheredUserGuid, false);

    expect(result).toEqual({ data: 'some-data' });
  });

  it('should throw if executeGraphQLQuery throws', async () => {
    (executeGraphQLQuery as jest.Mock).mockRejectedValue(new Error('fail'));

    await expect(
      getAccountPayments(token, accountId, page, size, tetheredUserGuid, false)
    ).rejects.toThrow('fail');
  });
});
