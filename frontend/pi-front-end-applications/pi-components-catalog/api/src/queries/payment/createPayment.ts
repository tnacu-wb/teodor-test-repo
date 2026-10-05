import { gql } from 'graphql-request';

export const INITIATE_PAYMENT_MUTATION = gql`
  mutation createPaymentMutation(
    $basketReference: String!
    $createPaymentCriteria: CreatePaymentCriteria!
  ) {
    initiatePayment(
      basketReference: $basketReference
      createPaymentCriteria: $createPaymentCriteria
    ) {
      status
      paymentRequiredDetails {
        paymentRedirect
        sessionId
        template
        providerUrl
      }
    }
  }
`;
