import { gql } from 'graphql-request';

export const CHECK_BASKET_STATUS = gql`
  query BasketStatus($basketReference: String!) {
    basketStatus(basketReference: $basketReference) {
      basketStatus
      retryPayment
      basketError {
        code
        description
        type
      }
    }
  }
`;
