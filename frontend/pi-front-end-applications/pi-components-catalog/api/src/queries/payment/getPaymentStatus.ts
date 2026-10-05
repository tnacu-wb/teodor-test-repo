import { gql } from 'graphql-request';

export const GET_PAYMENT_STATUS = gql`
  query GetPaymentStatus($basketReference: String!) {
    basket(basketReference: $basketReference) {
      status
    }
  }
`;
