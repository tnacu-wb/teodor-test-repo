import { gql } from 'graphql-request';

export const TABLE_RESERVATION = gql`
  mutation EventMutation(
    $adults: Int!
    $children: Int!
    $specialRequest: String!
    $date: String!
    $menuIds: [String]!
    $emailAddress: String!
    $firstname: String!
    $lastname: String!
    $occasionId: String!
    $telephoneNumber: String!
    $siteId: String!
    $time: String!
    $turnTimeMinutes: Int!
    $consentStatement: Boolean!
    $email: Boolean!
    $phone: Boolean!
    $postal: Boolean!
    $privacyStatement: Boolean!
    $profiling: Boolean!
    $pushNotification: Boolean!
    $sms: Boolean!
    $termsAndConditions: Boolean!
  ) {
    event(
      eventCriteria: {
        adults: $adults
        children: $children
        specialRequest: $specialRequest
        date: $date
        menuIds: $menuIds
        emailAddress: $emailAddress
        firstname: $firstname
        lastname: $lastname
        occasionId: $occasionId
        telephoneNumber: $telephoneNumber
        siteId: $siteId
        time: $time
        turnTimeMinutes: $turnTimeMinutes
        consent: {
          consentStatement: $consentStatement
          email: $email
          phone: $phone
          postal: $postal
          privacyStatement: $privacyStatement
          profiling: $profiling
          pushNotification: $pushNotification
          sms: $sms
          termsAndConditions: $termsAndConditions
        }
      }
    ) {
      bookingReference
      id
    }
  }
`;

export const TABLE_ENQUIRY = gql`
  mutation EnquiryMutation(
    $adults: Int!
    $children: Int!
    $specialRequest: String!
    $date: String!
    $menuIds: [String]!
    $emailAddress: String!
    $firstname: String!
    $lastname: String!
    $occasionId: String!
    $telephoneNumber: String!
    $siteId: String!
    $time: String!
    $turnTimeMinutes: Int!
    $consentStatement: Boolean!
    $email: Boolean!
    $phone: Boolean!
    $postal: Boolean!
    $privacyStatement: Boolean!
    $profiling: Boolean!
    $pushNotification: Boolean!
    $sms: Boolean!
    $termsAndConditions: Boolean!
  ) {
    enquiry(
      enquiryCriteria: {
        adults: $adults
        children: $children
        specialRequest: $specialRequest
        date: $date
        menuIds: $menuIds
        emailAddress: $emailAddress
        firstname: $firstname
        lastname: $lastname
        occasionId: $occasionId
        telephoneNumber: $telephoneNumber
        siteId: $siteId
        time: $time
        turnTimeMinutes: $turnTimeMinutes
        consent: {
          consentStatement: $consentStatement
          email: $email
          phone: $phone
          postal: $postal
          privacyStatement: $privacyStatement
          profiling: $profiling
          pushNotification: $pushNotification
          sms: $sms
          termsAndConditions: $termsAndConditions
        }
      }
    ) {
      bookingReference
      id
    }
  }
`;
