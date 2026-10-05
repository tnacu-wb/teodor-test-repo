import { gql } from 'graphql-request';

export const GET_ANCILLARIES_BOOKING_INFO = gql`
  query GetBookingInformation(
    $basketReference: String!
    $language: String!
    $country: String!
    $bookingChannelCriteria: BookingChannelCriteria!
  ) {
    bookingInformation(
      basketReference: $basketReference
      language: $language
      country: $country
      bookingChannelCriteria: $bookingChannelCriteria
    ) {
      hotelId
      totalCost
      currencyCode
      bookingFlowId
      infoMessages
      hasCityTax
      cityTaxTotal
      reservationByIdList {
        additionalGuestInfo {
          purposeOfStay
          acceptFutureMailing
        }
        reservationId
        roomStay {
          adultsNumber
          childrenNumber
          arrivalDate
          departureDate
          ratePlanCode
          rateExtraInfo {
            rateName
            rateDescription
          }
          roomExtraInfo {
            roomType
            roomName
          }
          accessibleRoom {
            isAccessible
            phoneNumber
          }
        }
        reservationGuestList {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            addressType
            cityName
            countryCode
            postalCode
          }
          email
          givenName
          isAccompanyingGuest
          nameTitle
          surName
        }
        billing {
          address {
            addressLine1
            addressLine2
            addressLine3
            addressLine4
            country
            postalCode
            companyName
            cityName
            countryCode
          }
          email
          firstName
          lastName
          telephone
          landline
          title
        }
      }
      upgradeToFlex {
        amount
        currency
        flexRateCode
      }
    }
  }
`;
