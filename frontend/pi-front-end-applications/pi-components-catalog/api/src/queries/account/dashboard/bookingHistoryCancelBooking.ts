import { gql } from 'graphql-request';

export const BOOKING_HISTORY_CANCEL_BOOKING = gql`
  mutation cancelBooking(
    $basketReference: String
    $bookingReference: String!
    $hotelId: String!
    $arrivalDate: String
    $country: String
    $language: String
    $bookingChannel: BookingChannelCriteria!
  ) {
    cancelBooking(
      cancelBookingRequest: {
        basketReference: $basketReference
        bookingReference: $bookingReference
        hotelId: $hotelId
        arrivalDate: $arrivalDate
        country: $country
        language: $language
        bookingChannel: $bookingChannel
      }
    ) {
      bookingReference
      cancellationId
    }
  }
`;
