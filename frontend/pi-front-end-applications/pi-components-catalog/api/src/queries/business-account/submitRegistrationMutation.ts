import { gql } from 'graphql-request';

export const submitRegistrationMutation = () => gql`
  mutation SubmitRegistration($registrationSubmitRequest: RegistrationSubmitRequest!) {
    submitRegistration(registrationSubmitRequest: $registrationSubmitRequest) {
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
      tetherDetails {
        accountNumber
        tetheredUserGuid
      }
    }
  }
`;
