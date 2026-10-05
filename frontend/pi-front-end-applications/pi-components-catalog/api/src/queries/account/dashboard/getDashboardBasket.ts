import { gql } from 'graphql-request';

export const GET_DASHBOARD_BASKET = gql`
  query basket($basketReference: String!) {
    basket(basketReference: $basketReference) {
      paymentOption
      status
      hotelId
      paymentOption
    }
  }
`;
