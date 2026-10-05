import { gql } from 'graphql-request';

export const getRegistrationInfoQuery = () => gql`
  query GetRegistrationInfo($registrationCode: String!) {
    getRegistrationInfo(registrationCode: $registrationCode) {
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
