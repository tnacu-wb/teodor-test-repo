import { gql } from 'graphql-request';

export const getAccountPaymentsQuery = () => gql`
  query getPaymentInfo($paymentInfoCriteria: PaymentInfoCriteria!) {
    getPaymentInfo(paymentInfoCriteria: $paymentInfoCriteria) {
      payments {
        paymentDate
        paymentDescription
        failureReason
        paymentFailed
        paymentValue {
          value
          currencyCode
          currencySymbol
        }
      }
      errors
    }
  }
`;
