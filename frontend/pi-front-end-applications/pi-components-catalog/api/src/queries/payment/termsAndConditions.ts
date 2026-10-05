import { gql } from 'graphql-request';

export const GET_TERMS_AND_CONDITIONS_QUERY = gql`
  query getTermsAndConditions(
    $country: String!
    $language: String!
    $hotelId: String!
    $rateCode: String!
    $bookingChannel: String!
  ) {
    termsAndConditions(
      bookingFlowCriteria: {
        hotelId: $hotelId
        language: $language
        country: $country
        rateCode: $rateCode
        bookingChannel: $bookingChannel
      }
    ) {
      text
    }
  }
`;
