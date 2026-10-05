import { gql } from 'graphql-request';

export const CREATE_RESERVATION_GUEST_CCUI = gql`
  mutation createReservationGuest(
    $hotelId: String!
    $reasonForStay: String!
    $companyName: String
    $addressLine1: String
    $addressLine2: String
    $addressLine3: String
    $addressLine4: String
    $addressType: String
    $countryCode: String
    $postalCode: String
    $cityName: String
    $emailAddress: String
    $firstName: String!
    $landline: String
    $lastName: String!
    $mobile: String!
    $title: String
    $basketReference: String!
    $acceptFutureMailing: Boolean
    $stayingGuests: [StayingGuest!]!
    $sendEmailConfirmation: Boolean
    $sendEmailInvoice: Boolean
  ) {
    createReservationGuest(
      createReservationGuestCriteria: {
        basketReference: $basketReference
        hotelId: $hotelId
        reasonForStay: $reasonForStay
        booker: {
          title: $title
          firstName: $firstName
          lastName: $lastName
          emailAddress: $emailAddress
          mobile: $mobile
          landline: $landline
          acceptFutureMailing: $acceptFutureMailing
          address: {
            companyName: $companyName
            addressLine1: $addressLine1
            addressLine2: $addressLine2
            addressLine3: $addressLine3
            addressLine4: $addressLine4
            addressType: $addressType
            countryCode: $countryCode
            postalCode: $postalCode
            cityName: $cityName
          }
        }
        stayingGuests: $stayingGuests
        sendEmailConfirmation: $sendEmailConfirmation
        sendEmailInvoice: $sendEmailInvoice
      }
    ) {
      basketReference
    }
  }
`;
