import { gql } from 'graphql-request';

export const BOOK_MUTATION = gql`
  mutation createReservation(
    $reservations: [RoomReservation!]!
    $bookingChannel: BookingChannelCriteria!
    $bookingFlowId: String
  ) {
    createReservation(
      createReservationCriteria: {
        reservations: $reservations
        bookingChannel: $bookingChannel
        bookingFlowId: $bookingFlowId
      }
    ) {
      basketReference
    }
  }
`;
