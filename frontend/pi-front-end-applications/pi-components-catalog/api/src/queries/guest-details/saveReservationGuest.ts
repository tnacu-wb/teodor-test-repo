import { gql } from 'graphql-request';

export const SAVE_RESERVATION_GUEST = gql`
  input SaveReservationGuestCriteria {
    basketReference: String!
    hotelId: String!
    reasonForStay: String!
    booker: Booker!
    stayingGuests: [StayingGuest!]!
  }

  input StayingGuest {
    sameAsBooker: Boolean!
    stayingGuestDetails: StayingGuestDetails
  }

  input StayingGuestDetails {
    title: String!
    firstName: String!
    lastName: String!
  }

  input Booker {
    title: String!
    firstName: String!
    lastName: String!
    emailAddress: String!
    mobile: String
    landline: String
    acceptFutureMailing: Boolean
    address: GuestAddress
  }

  input GuestAddress {
    addressType: String
    postalCode: String
    addressLine1: String
    addressLine2: String
    addressLine3: String
    addressLine4: String
    countryCode: String
  }
  mutation saveReservationGuest($saveReservationGuestCriteria: SaveReservationGuestCriteria!) {
    saveReservationGuest(saveReservationGuestCriteria: $saveReservationGuestCriteria) {
      basketReference
    }
  }
`;
