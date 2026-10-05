import { gql } from 'graphql-request';

export const GET_BOOKING_HISTORY_DETAILS = gql`
  query getBookingHistory(
    $arrival: String!
    $bookingReference: String!
    $hotelId: String!
    $surname: String!
    $country: String
    $language: String
    $bookingChannel: BookingChannelCriteria
    $sourceSystem: String
  ) {
    bookingInfoCardDetails(
      bookingInfoCardRequest: {
        arrival: $arrival
        bookingReference: $bookingReference
        hotelId: $hotelId
        surname: $surname
        country: $country
        language: $language
        bookingChannel: $bookingChannel
        sourceSystem: $sourceSystem
      }
    ) {
      reservationDetails {
        basketReference
        bookingReference
        hotelCode
        arrivalDate
        bookingStatus
        departureDate
        nights
        noOfRooms
        rateType
        hotelHasCityTaxForLeisure
        paymentOption
        sourceSystem
        rateDescription
        cancellationInfoResponse {
          amendable
          cancelable
          ruleCompliant
          aemLabelKey
        }
        donationsPackage {
          description
          noSelections
          packageCode
          totalPrice {
            amount
            currency
          }
        }
        newTotal {
          amount
          currency
        }
        outstandingAmount {
          amount
          currency
        }
        prepaidAmount {
          amount
          currency
        }
        previousTotal {
          amount
          currency
        }
        refund {
          amount
          currency
        }
        rooms {
          adults
          adultsMeal {
            description
            noSelections
            packageCode
            totalPrice {
              amount
              currency
            }
          }
          extrasItems {
            description
            noSelections
            packageCode
            totalPrice {
              amount
              currency
            }
          }
          children
          cot
          guest {
            firstName
            lastName
            title
          }
          kidsMeal {
            description
            noSelections
            packageCode
            totalPrice {
              amount
              currency
            }
          }
          roomCost {
            amount
            currency
          }
          roomId
          roomType
        }
        totalCost {
          amount
          currency
        }
        payment {
          cardType
        }
        dinnerAllowance {
          amount
          currency
        }
      }
      checkInTime
      checkOutTime
    }
  }
`;
