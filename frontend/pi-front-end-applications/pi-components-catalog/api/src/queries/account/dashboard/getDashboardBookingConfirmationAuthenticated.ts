import { gql } from 'graphql-request';

export const GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED = gql`
  query bookingConfirmationAuthenticated(
    $bookingReference: String!
    $language: String!
    $country: String!
    $bookingChannel: String
  ) {
    bookingConfirmationAuthenticated(
      bookingReference: $bookingReference
      language: $language
      country: $country
      bookingChannel: $bookingChannel
    ) {
      reservationByIdList {
        reservationId
        billing {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            addressType
            cityName
            companyName
            country
            countryCode
            postalCode
          }
          title
          telephone
          landline
          firstName
          lastName
          email
        }
        reservationGuestList {
          givenName
          surName
        }
        gdsReferenceNumber
        roomStay {
          checkInTime
          checkOutTime
          ratePlanCode
          arrivalDate
          departureDate
          bookingChannel
          roomPrice
          cot
          adultsNumber
          roomExtraInfo {
            roomName
          }
          childrenNumber
        }
        paymentCard {
          cardNumberMasked
          paymentMethod
        }
        reservationOverrideReasons {
          reasonCode
          callerName
          managerName
          reasonName
        }
        reservationOverridden
        guaranteeCode
        reservationStatus
        additionalGuestInfo {
          purposeOfStay
        }
      }
      balanceOutstanding
      currencyCode
      newTotal
      policyCode
      previousTotal
      totalCost
      cityTaxTotal
      hotelId
      hotelName
      bookingFlowId
      rateMessage
      bookingReference
      basketReference
    }
  }
`;
