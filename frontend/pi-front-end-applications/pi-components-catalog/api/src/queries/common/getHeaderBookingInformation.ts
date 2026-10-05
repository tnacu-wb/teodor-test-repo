import { gql } from 'graphql-request';

export const GET_HEADER_BOOKING_INFORMATION = gql`
  query GetBookingInformation(
    $basketReference: String!
    $language: String!
    $country: String!
    $bookingChannelCriteria: BookingChannelCriteria!
  ) {
    bookingInformation(
      basketReference: $basketReference
      language: $language
      country: $country
      bookingChannelCriteria: $bookingChannelCriteria
    ) {
      bookingFlowId
      hotelId
    }
  }
`;
