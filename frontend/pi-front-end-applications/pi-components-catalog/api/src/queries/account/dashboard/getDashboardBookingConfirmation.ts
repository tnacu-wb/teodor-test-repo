import { gql } from 'graphql-request';

export const GET_DASHBOARD_BOOKING_CONFIRMATION = gql`
  query bookingConfirmation(
    $basketReference: String!
    $language: String!
    $country: String!
    $bookingChannel: String
  ) {
    bookingConfirmation(
      basketReference: $basketReference
      language: $language
      country: $country
      bookingChannel: $bookingChannel
    ) {
      reservationByIdList {
        reservationId
        billing {
          address {
            addressLine1
            postalCode
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
      bookingSpinnerConfig {
        order
        seconds
        text
      }
    }
  }
`;
