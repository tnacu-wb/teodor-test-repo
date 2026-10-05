import { gql } from 'graphql-request';

export const GET_ENQUIRY_INFO_BY_ID = gql`
  query enquiryById($id: String!) {
    enquiryById(id: $id) {
      id
      adults
      bookingReference
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
      lastname
      occasionId
      occasionName
      paymentLink
      siteId
      siteName
      telephoneNumber
      time
      turnTimeMinutes
    }
  }
`;
