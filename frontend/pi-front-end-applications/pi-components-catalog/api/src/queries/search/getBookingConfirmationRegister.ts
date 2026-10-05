import { gql } from 'graphql-request';

export const GET_REGISTER_BOOKING_CONFIRMATION = gql`
  query GetRegisterPageBookingConfirmation(
    $basketReference: String!
    $country: String!
    $language: String!
    $bookingChannel: String!
    $flow: String
  ) {
    bookingConfirmation(
      basketReference: $basketReference
      country: $country
      language: $language
      bookingChannel: $bookingChannel
      flow: $flow
    ) {
      reservationByIdList {
        reservationBooker {
          title
          firstName
          lastName
          address {
            companyName
            addressType
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            cityName
            countryCode
            postalCode
          }
          email
          mobile
          landline
        }
      }
    }
  }
`;
