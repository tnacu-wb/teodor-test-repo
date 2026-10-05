import { gql } from 'graphql-request';

export const GET_HEADER_BOOKING_FLOW_INFORMATION = gql`
  query GetBookingFlowInformation(
    $bookingFlowId: String!
    $hotelId: String!
    $language: String!
    $country: String!
  ) {
    bookingFlowInformation(
      bookingFlowId: $bookingFlowId
      hotelId: $hotelId
      language: $language
      country: $country
    ) {
      brand
      bookingFlowSteps {
        id
        step
        title
      }
    }
  }
`;
