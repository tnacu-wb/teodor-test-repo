import { gql } from 'graphql-request';

export const getActivationDetailsQuery = () => gql`
  query GetActivationDetails($activationKey: String!) {
    getActivationDetails(activationKey: $activationKey) {
      emailAddress
      accessLevel
      address {
        postCode
        country
        addressLine5
        addressLine4
        addressLine3
        addressLine2
        addressLine1
      }
      ghNumber
      id
      position
      phoneNumber
      mobileNumber
      textConfirmation
      title
      centralCardId
      password
      lockedForEditing
      guestHistoryNumber
      employeeAnswers {
        customerReferenceAnswer
        purchaseOrderAnswer
        userDefinedAnswers {
          miAnswer
          miID
        }
      }
      employeeStatus
      dialingCode
    }
  }
`;
