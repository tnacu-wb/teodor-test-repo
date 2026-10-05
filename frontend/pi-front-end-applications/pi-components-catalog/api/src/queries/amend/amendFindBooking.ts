import { gql } from 'graphql-request';

export const AMEND_FIND_BOOKING = gql`
  query amendFindBooking(
    $arrivalDate: String!
    $country: String
    $language: String
    $lastName: String!
    $resNo: String!
    $bookingChannel: BookingChannelCriteria
  ) {
    findBooking(
      findBookingCriteria: {
        arrivalDate: $arrivalDate
        country: $country
        language: $language
        lastName: $lastName
        resNo: $resNo
        bookingChannel: $bookingChannel
      }
    ) {
      cookieName
      redirectBase
      ref
      sourcePms
      token
      minutesTillExpiry
      basketReference
      operaConfNumber
    }
  }
`;
