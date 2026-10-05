import { gql } from 'graphql-request';

export const viewCustomerInvoicesQuery = () => gql`
  mutation ViewCustomerInvoicesV2($payload: CustomerAccountInvoiceRequest!) {
    viewCustomerInvoicesV2(payload: $payload) {
      response {
        invoices {
          statementDate
          invoiceNo
          broughtForward {
            amount
            currencyCode
            currencySymbol
          }
          paymentsReceived {
            amount
            currencyCode
            currencySymbol
          }
          overdueBalance {
            amount
            currencyCode
            currencySymbol
          }
          invoiceValue {
            amount
            currencyCode
            currencySymbol
          }
          statementBalance {
            amount
            currencyCode
            currencySymbol
          }
          fileAutoID
        }
      }
      pagingResult {
        toRecord
        totalRecordCount
        fromRecord
        lastPage
      }
    }
  }
`;
