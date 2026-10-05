import { gql } from 'graphql-request';

export const GET_BASKET_STATUS = gql`
  query GetBasketStatus($basketReference: String!) {
    basket(basketReference: $basketReference) {
      status
    }
  }
`;
