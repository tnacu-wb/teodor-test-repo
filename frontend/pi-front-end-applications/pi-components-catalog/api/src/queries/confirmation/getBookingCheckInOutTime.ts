import { gql } from 'graphql-request';

export const GET_BOOKING_CHECK_IN_OUT_TIME = gql`
  query GetBookingCheckInOutTime($basketReference: String!, $country: String!, $language: String!) {
    bookingConfirmation(basketReference: $basketReference, country: $country, language: $language) {
      hotelId
      reservationByIdList {
        roomStay {
          checkInTime
          checkOutTime
          arrivalDate
          departureDate
        }
      }
    }
  }
`;
