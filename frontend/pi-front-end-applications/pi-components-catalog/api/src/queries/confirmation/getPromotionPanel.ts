import { gql } from 'graphql-request';

export const GET_PROMOTION_PANEL = gql`
  query GetPromotionPanel(
    $hotelId: String!
    $language: String!
    $country: String!
    $rateCode: String!
    $bookingChannel: String!
  ) {
    promotionPanel(
      bookingFlowCriteria: {
        hotelId: $hotelId
        language: $language
        country: $country
        rateCode: $rateCode
        bookingChannel: $bookingChannel
      }
    ) {
      description
      image
      linkLabel
      linkPath
      name
    }
  }
`;
