import { gql } from 'graphql-request';

export const AMEND_EDIT_ROOM = gql`
  mutation AmendEditRoom(
    $reservationId: String!
    $tempBookingRef: String!
    $roomType: String!
    $channel: Channel!
    $subchannel: String!
    $language: String
    $adultsNumber: Int!
    $childrenNumber: Int!
    $cotRequired: Boolean
    $title: String!
    $firstName: String!
    $lastName: String!
    $token: String!
    $emailAddress: String
    $addressLine1: String
    $addressLine2: String
    $addressLine3: String
    $cityName: String
    $postalCode: String
    $countryCode: String
  ) {
    amendEditRoom(
      editRoomCriteria: {
        reservationId: $reservationId
        tempBookingRef: $tempBookingRef
        roomOccupancy: {
          adultsNumber: $adultsNumber
          childrenNumber: $childrenNumber
          cotRequired: $cotRequired
        }
        leadGuest: {
          title: $title
          firstName: $firstName
          lastName: $lastName
          emailAddress: $emailAddress
          address: {
            addressLine1: $addressLine1
            addressLine2: $addressLine2
            addressLine3: $addressLine3
            cityName: $cityName
            postalCode: $postalCode
            countryCode: $countryCode
            addressType: "HOME"
          }
        }
        roomType: $roomType
        bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
        token: $token
      }
    ) {
      tempBookingRef
    }
  }
`;
