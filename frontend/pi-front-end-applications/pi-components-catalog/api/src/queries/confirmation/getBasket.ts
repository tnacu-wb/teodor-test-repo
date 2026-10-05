import { gql } from 'graphql-request';

export const GET_BASKET = gql`
  query GetBasket($basketReference: String!) {
    basket(basketReference: $basketReference) {
      paymentOption
      status
      sendMail
    }
  }
`;
