import { gql } from 'graphql-request';

export const BART_BOOKING_INFORMATION = gql`
  query GetBartBookingInformation(
    $bookingReference: String!
    $bookerLastName: String!
    $arrivalDate: String!
    $language: String
    $country: String
  ) {
    bartBookingInformation(
      bartBookingInformationCriteria: {
        bookingReference: $bookingReference
        bookerLastName: $bookerLastName
        arrivalDate: $arrivalDate
        country: $country
        language: $language
      }
    ) {
      bookingChannel
      bookingType
      prepaid
      rateName
      rateMessage
      prepaidAmount {
        amount
        currencyCode
      }
      hasCityTax
      paymentOption
      totalCost {
        amount
        currencyCode
      }
      donations {
        amount
        currencyCode
      }
      balanceOutstanding {
        currencyCode
        amount
      }
      rooms {
        adultsNumber
        arrivalDate
        childrenNumber
        departureDate
        packages {
          postingDate
          quantity
          description
          id
          imageSrc
          name
          unitCost {
            amount
            currencyCode
          }
        }
        reservationGuest {
          givenName
          surName
        }
        totalRoomCost {
          amount
          currencyCode
        }
        roomType
      }
      totalCost {
        amount
        currencyCode
      }
    }
  }
`;
