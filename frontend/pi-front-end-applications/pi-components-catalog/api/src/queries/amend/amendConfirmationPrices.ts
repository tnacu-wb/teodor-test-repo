import { gql } from 'graphql-request';

export const GET_AMEND_CONFIRMATION_PRICES = gql`
  query AmendConfirmationPrices(
    $originalBookingRef: String!
    $tempBookingRef: String!
    $token: String!
  ) {
    amendConfirmationPrices(
      amendConfirmationPricesRequest: {
        originalBookingRef: $originalBookingRef
        tempBookingRef: $tempBookingRef
        token: $token
      }
    ) {
      previousTotal
      newTotalCost
      outstandingBalance
    }
  }
`;
