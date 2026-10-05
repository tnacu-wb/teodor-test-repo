import { gql } from 'graphql-request';

export const INITIATE_PAYPAL_PAYMENT_MUTATION = gql`
  mutation createPaypalPaymentMutation(
    $basketReference: String!
    $createPaymentCriteria: CreatePaymentCriteria!
  ) {
    initiatePaypalPayment(
      basketReference: $basketReference
      createPaymentCriteria: $createPaymentCriteria
    ) {
      status
      paymentRequiredDetails {
        paymentRedirect
        sessionId
        template
      }
    }
  }
`;
