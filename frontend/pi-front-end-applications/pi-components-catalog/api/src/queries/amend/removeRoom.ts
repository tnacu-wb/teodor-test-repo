import { gql } from 'graphql-request';

export const REMOVE_ROOM = gql`
  mutation removeRoom(
    $tempBookingRef: String!
    $reservationId: String!
    $channel: Channel!
    $subchannel: String!
    $token: String!
    $language: String
  ) {
    removeRoom(
      tempBookingRef: $tempBookingRef
      reservationId: $reservationId
      bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
      token: $token
    ) {
      tempBookingRef
    }
  }
`;
