import { gql } from 'graphql-request';

export const GET_DASHBOARD_DONATIONS_PACKAGES = gql`
  query GetDashboardPackages(
    $country: String!
    $language: String!
    $hotelId: String!
    $rateCode: String!
    $bookingChannel: String
  ) {
    donations(
      bookingFlowCriteria: {
        country: $country
        language: $language
        hotelId: $hotelId
        rateCode: $rateCode
        bookingChannel: $bookingChannel
      }
    ) {
      donationPackages {
        code
        currency
        unitPrice
      }
    }
  }
`;
