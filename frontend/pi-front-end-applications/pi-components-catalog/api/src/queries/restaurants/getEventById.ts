import { gql } from 'graphql-request';

export const GET_EVENT_INFO_BY_ID = gql`
  query eventById($id: String!) {
    eventById(id: $id) {
      adults
      areaId
      areaName
      bookingReference
      braintreeCustomerId
      cancelLink
      children
      consent {
        consentStatement
        email
        phone
        postal
        privacyStatement
        profiling
        pushNotification
        sms
        termsAndConditions
      }
      date
      editLink
      emailAddress
      firstname
      id
      lastname
      name
      occasionId
      occasionName
      siteName
      siteId
      siteTimezone
      specialRequest
      telephoneNumber
      time
      turnTimeMinutes
    }
  }
`;
