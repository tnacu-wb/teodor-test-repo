import { gql } from 'graphql-request';

export const authenticateRegistrationMutation = () => gql`
  mutation AuthenticateRegistration(
    $registrationCode: String!
    $authenticationAnswers: [AuthenticationAnswer]
  ) {
    authenticateRegistration(
      authenticateRegistrationRequest: {
        registrationCode: $registrationCode
        authenticationAnswers: $authenticationAnswers
      }
    ) {
      registrationPrePopulatedItems {
        emailAddress
        forename
        landlineNumber
        mobileNumber
        surname
        title
      }
      registrationCodeInfo {
        authenticationQuestions {
          question
          questionId
        }
        primarySchemeCustomerId
        registrationCode
        registrationRole
        schemeCustomerId
      }
    }
  }
`;
