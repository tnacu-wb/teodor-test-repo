import { gql } from 'graphql-request';

export const GET_PRECHECKIN_BOOKING_INFORMATION = gql`
  query bookingConfirmation($basketReference: String!, $language: String!, $country: String!) {
    bookingConfirmation(basketReference: $basketReference, language: $language, country: $country) {
      reservationByIdList {
        reservationId
        reservationStatus
        preCheckInStatus
        deRegCardCompleted
        billing {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            cityName
            postalCode
            countryCode
            country
            addressType
          }
          title
          telephone
          firstName
          lastName
          email
        }
        reservationGuestList {
          givenName
          surName
          email
          nameTitle
          additionalDetails {
            dob
            passportNumber
            nationality
          }
          address {
            addressType
            addressLine1
            addressLine2
            addressLine4
            addressType
            cityName
            countryCode
            postalCode
            addressId
          }
          homeAddress {
            addressType
            addressLine1
            addressLine2
            addressLine4
            addressType
            cityName
            countryCode
            postalCode
            addressId
          }
          profileId
        }
        gdsReferenceNumber
        roomStay {
          arrivalDate
          departureDate
          childrenNumber
          adultsNumber
          roomType
          roomExtraInfo {
            roomName
          }
        }
        additionalGuestInfo {
          purposeOfStay
        }
      }
      hotelId
      hotelName
      bookingFlowId
      rateMessage
      bookingReference
      basketReference
    }
  }
`;
