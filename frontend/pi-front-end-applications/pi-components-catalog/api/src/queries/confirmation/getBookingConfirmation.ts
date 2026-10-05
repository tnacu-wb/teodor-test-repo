import { gql } from 'graphql-request';

export const GET_BOOKING_CONFIRMATION = gql`
  query GetBookingConfirmation(
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
      balanceOutstanding
      bookingFlowId
      currencyCode
      hotelId
      infoMessages
      newTotal
      policyCode
      totalCost
      cityTaxTotal
      bookingReference

      bookingSpinnerConfig {
        order
        seconds
        text
      }

      reservationByIdList {
        additionalGuestInfo {
          purposeOfStay
        }

        reservationPackageList {
          description
          unitPrice
          totalQuantity
          computedPrice
        }

        depositPolicies {
          amountDue {
            amount
            currencyCode
          }
          amountPaid {
            amount
            currencyCode
          }
          policyCode
        }

        reservationGuestList {
          givenName
          surName
          nameTitle
        }

        roomStay {
          roomPrice
          adultsNumber
          arrivalDate
          childrenNumber
          departureDate
          roomType
          cot
          ratePlanCode

          ratesPerNight {
            startDate
            pricePerNight
            cityTaxPerNight
          }

          rateExtraInfo {
            rateDescription
            rateLongDescription
            rateName
          }
          roomExtraInfo {
            roomDescription
            roomType
            roomName
          }
        }

        billing {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            country
            postalCode
          }
          title
          telephone
          lastName
          firstName
          email
        }
      }
    }
  }
`;
