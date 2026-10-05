import { gql } from 'graphql-request';

export const ADD_NEW_ROOM = gql`
  mutation AddNewRoom(
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
    $ratePlanCode: String
    $specialRequests: [String]
    $addressLine1: String
    $addressLine2: String
    $addressLine3: String
    $addressLine4: String
    $cityName: String
    $postalCode: String
    $countryCode: String
  ) {
    addNewRoom(
      addNewRoomCriteria: {
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
            addressLine4: $addressLine4
            cityName: $cityName
            postalCode: $postalCode
            countryCode: $countryCode
            addressType: "HOME"
          }
        }
        roomType: $roomType
        bookingChannel: { channel: $channel, subchannel: $subchannel, language: $language }
        token: $token
        ratePlanCode: $ratePlanCode
        specialRequests: $specialRequests
      }
    ) {
      tempBookingRef
    }
  }
`;
