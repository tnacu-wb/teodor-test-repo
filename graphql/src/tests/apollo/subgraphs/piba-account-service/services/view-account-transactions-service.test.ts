import { post } from '../../../../../../src/apollo/client/rest-client';
import { viewAccountTransactions } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/view-account-transactions-service';
import { endpoints } from '../../../../../apollo/subgraphs/piba-account-service-opera/services/base-service';

jest.mock('../../../../../apollo/client/rest-client');

describe('viewAccountTransactions Resolver', () => {
  const viewAccountTransactionsRequest = {
    tetheredUserGuid: 'b0f1adb4-4421-4bad-aa6c-20e173674518',
    scheme: 'GB',
    schemeCustomerId: 784237,
    pagingRequest: {
      page: 1,
      maximumDisplayRows: 1
    },
    searchCriteria: {
      dateSearch: {
        dateFrom: '2024-01-01',
        dateTo: '2025-09-18',
        transactionTypes: 'Both'
      }
    }
  };

  it('should call the post function when correct parameters are provided', async () => {
    const mockResponse = {
      data: {
        viewAccountTransactions: {
          pagingResult: {
            fromRecord: 1,
            lastPage: 18,
            totalRecordCount: 18,
            totalRecords: null
          },
          response: {
            transactions: [
              {
                invoiceDate: '2025-04-21',
                invoiceNo: '414210',
                transactionDate: '2025-04-13T19:15:00',
                netAmount: {
                  amount: -7.92,
                  currencyCode: 'GBP',
                  currencySymbol: '£'
                },
                taxAmount: {
                  amount: -1.58,
                  currencyCode: 'GBP',
                  currencySymbol: '£'
                },
                grossAmount: {
                  amount: -9.5,
                  currencyCode: 'GBP',
                  currencySymbol: '£'
                },
                location: 'Cardiff North',
                pan: '30895001*******0025',
                cardName: 'Mr Ramesh Patil',
                purchaseOrderReference: '76770302',
                customerOwnRef: '',
                salesOrderNumber: '76770302',
                lineItems: [
                  {
                    grossAmount: {
                      amount: -9.5,
                      currencyCode: 'GBP',
                      currencySymbol: '£'
                    },
                    description: 'Good Will G Org Inv No 414183',
                    guestName: 'Seema Patil',
                    invoiceLineItem: 2,
                    netAmount: {
                      amount: -7.92,
                      currencyCode: 'GBP',
                      currencySymbol: '£'
                    },
                    quantity: 1,
                    taxAmount: {
                      amount: -1.58,
                      currencyCode: 'GBP',
                      currencySymbol: '£'
                    }
                  }
                ]
              }
            ]
          }
        }
      }
    };
    (post as jest.Mock).mockResolvedValueOnce(mockResponse);

    const response = await viewAccountTransactions({ payload: viewAccountTransactionsRequest }, {});

    expect(post).toHaveBeenCalledWith(
      endpoints.VIEW_ACCOUNT_TRANSACTIONS,
      viewAccountTransactions,
      viewAccountTransactionsRequest,
      {}
    );
    expect(response).toEqual(mockResponse);
  });

  it('should handle null response when it is returned gracefully', async () => {
    (post as jest.Mock).mockResolvedValueOnce(null);

    const response = await viewAccountTransactions({ payload: viewAccountTransactionsRequest }, {});

    expect(response).toEqual(null);
  });
});
