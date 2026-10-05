import { gql } from 'graphql-request';

export const GET_DONATIONS_QUERY = gql`
  query getDonations(
    $country: String!
    $language: String!
    $hotelId: String!
    $rateCode: String!
    $bookingChannel: String!
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
      description
      imageSrc
      name
      informationBox
      donationPackages {
        code
        currency
        unitPrice
      }
    }
  }
`;
