import { gql } from 'graphql-request';

export const GET_BOOKING_INFORMATION = gql`
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
      newTotal
      previousTotal
      totalCostWoDiscount
      discount
      cityTaxTotal
      infoMessages
      reservationByIdList {
        reservationId
        additionalGuestInfo {
          purposeOfStay
        }
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
          givenName
          surName
          nameTitle
          email
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
          }
          email
          firstName
          lastName
          telephone
          landline
          title
        }
      }
    }
  }
`;
