import { gql } from 'graphql-request';

export const GET_BOOKING_CONFIRMATION_AMEND = gql`
  query getBookingConfirmationAmend(
    $basketReference: String!
    $country: String!
    $language: String!
  ) {
    bookingConfirmation(basketReference: $basketReference, country: $country, language: $language) {
      bookingFlowId
      hotelId
      hotelName
      infoMessages
      currencyCode
      totalCost
      cityTaxTotal
      previousTotal
      newTotal
      channel
      companyId
      reservationByIdList {
        reservationId
        preCheckInStatus
        deRegCardCompleted
        reservationGuestList {
          givenName
          surName
          nameTitle
          email
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            cityName
            postalCode
            countryCode
          }
        }
        billing {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            companyName
            country
            postalCode
          }
          email
        }
        roomStay {
          adultsNumber
          childrenNumber
          arrivalDate
          departureDate
          ratePlanCode
          roomExtraInfo {
            roomType
            roomName
            groupId
          }
          accessibleRoom {
            phoneNumber
            isAccessible
          }
          roomPrice
        }
      }
    }
  }
`;
