import { gql } from 'graphql-request';

export const getEmployeeByIdQuery = () => gql`
  query getEmployeeDetailsV3($companyId: String!, $employeeId: String!, $activationKey: String) {
    getEmployeeDetailsV3(
      companyId: $companyId
      employeeId: $employeeId
      activationKey: $activationKey
    ) {
      id
      ghNumber
      emailAddress
      position
      phoneNumber
      mobileNumber
      textConfirmation
      title
      firstName
      lastName
      centralCardId
      address {
        addressLine1
        addressLine2
        addressLine3
        addressLine4
        addressLine5
        postCode
        country
      }
      accessLevel
      employeeStatus
      dialingCode
      employeeAnswers {
        customerReferenceAnswer
        purchaseOrderAnswer
        userDefinedAnswers {
          miID
          miAnswer
        }
      }
      guestHistoryNumber
      lockedForEditing
      password
    }
  }
`;
