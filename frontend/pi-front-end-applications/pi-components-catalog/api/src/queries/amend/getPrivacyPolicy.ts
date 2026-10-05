import { gql } from 'graphql-request';

export const GET_PRIVACY_POLICY = gql`
  query getPrivacyPolicy(
    $hotelId: String!
    $language: String!
    $country: String!
    $rateCode: String!
    $bookingChannel: String!
  ) {
    privacyPolicy(
      bookingFlowCriteria: {
        hotelId: $hotelId
        language: $language
        country: $country
        rateCode: $rateCode
        bookingChannel: $bookingChannel
      }
    ) {
      description
      linkLabel
      linkSrc
      moreInfoLabel
      moreInfo {
        description
        image
      }
      name
    }
  }
`;
