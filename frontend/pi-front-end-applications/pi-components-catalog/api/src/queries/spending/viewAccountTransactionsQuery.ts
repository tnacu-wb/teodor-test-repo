import { gql } from 'graphql-request';

export const viewAccountTransactionsQuery = () => gql`
  mutation ViewAccountTransactions($payload: CustomerAccountTransactionsRequest!) {
    viewAccountTransactions(payload: $payload) {
      pagingResult {
        fromRecord
        lastPage
        totalRecordCount
        totalRecords
      }
      response {
        transactions {
          invoiceDate
          invoiceNo
          transactionDate
          netAmount {
            amount
            currencyCode
            currencySymbol
          }
          taxAmount {
            amount
            currencyCode
            currencySymbol
          }
          grossAmount {
            amount
            currencyCode
            currencySymbol
          }
          location
          pan
          cardName
          purchaseOrderReference
          customerOwnRef
          salesOrderNumber
          lineItems {
            grossAmount {
              amount
              currencyCode
              currencySymbol
            }
            description
            guestName
            invoiceLineItem
            netAmount {
              amount
              currencyCode
              currencySymbol
            }
            quantity
            taxAmount {
              amount
              currencyCode
              currencySymbol
            }
          }
        }
      }
    }
  }
`;
