import { gql } from 'graphql-request';

export const COPY_BOOKING = gql`
  mutation copyBooking(
    $originalBasketReference: String!
    $bookingChannel: BookingChannelCriteria!
    $token: String!
  ) {
    copyBooking(
      copyBookingCriteria: {
        originalBasketReference: $originalBasketReference
        bookingChannel: $bookingChannel
        token: $token
      }
    ) {
      copyBasketReference
    }
  }
`;
